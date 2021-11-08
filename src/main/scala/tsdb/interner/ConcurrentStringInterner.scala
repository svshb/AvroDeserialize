package tsdb.interner

import java.nio.charset.StandardCharsets
import java.util
import java.util.concurrent.atomic.{AtomicInteger, AtomicLongArray, AtomicReferenceArray}
import java.util.concurrent.locks.LockSupport

/**
  * Class for concurrent string interning using optimistic locking.
  * Contract:
  * 1) Fast under high concurrency
  * 2) A very small chance to reject legit interning
  * 3) Only insert and get, no delete/update
  * 4) Fixed size (no resizes)
  * 5) Simple concurrent model
  *
  * While updates are concurrent-safe, there are a small chance that if 2 threads will try to insert
  * same element, one will starve cycles waiting for node to be updated while second thread is descheduled and
  * cannot finalize write (as we do optimistic locking on write, both threads CAS, one succeeds, another spin loops).
  * We take these odds as this should be a very rare occasion (only on writes and we are heavily
  * read skewed), we can hint waiting thread to sleep and it's better than doing buggy shenanigans with
  * thread signalling risking locking thread forever.
  *
  * Concurrency model relies on JMM/happens-before:
  * https://docs.oracle.com/javase/specs/jls/se11/html/jls-17.html (17.4.5. Happens-before Order)
  *
  * Instead of costly locking we spin loop on write contention with forced thread descheduling to not waste CPU too much
  * This should be a rare occasion given that we are read heavy, so it's fine to be not 100% performant
  * Also the actual time spent in park is OS dependent:
  * https://hazelcast.com/blog/locksupport-parknanos-under-the-hood-and-the-curious-case-of-parking/
  * Theoretically we can use Thread.yield() or Thread.onSpinWait(), but they are mere hints
  * Parking will force thread to give up CPU share
  * https://www.javamex.com/tutorials/threads/yield.shtml
  *
  * @param bucketCount amount of buckets in open-addressing hash map, it can hold up to bucketCount/2 elements
  *                    due to protection from map overload and get taking too much time
  */
class ConcurrentStringInterner(private val bucketCount: Int) extends StringInterner {
  assert(bucketCount > 1, s"Bucket count must be positive and more than 1")

  protected type Utf8String = Array[Byte]
  // bucket represents one value in open addressing hash map
  // upper 4 bytes are string id, which are also index in key/string arrays
  // string ids generated are always positive, meaning that correct bucket value is positive
  protected type Bucket = Long
  // lower 4 bytes of bucket
  protected type Hash = Int
  // index of bucket
  protected type Index = Int

  // no value is stored in bucket
  private val Empty: Bucket = 0L
  // bucket is under update right now
  // will never clash with real value as real values are always positive
  private val Locked: Bucket = -1L

  // Hashmap implementation with open addressing conflict resolution
  // Each node is either empty or assigned once, protected by CAS and marking
  private val buckets: AtomicLongArray = new AtomicLongArray(bucketCount)
  private val keys: AtomicReferenceArray[Utf8String] =
    new AtomicReferenceArray[Utf8String](bucketCount)
  private val strings: AtomicReferenceArray[String] =
    new AtomicReferenceArray[String](bucketCount)
  // we start id with 1 to never use id 0 (as 0 is our Empty bucket value)
  private val nextFreeId: AtomicInteger = new AtomicInteger(1)

  // here hb(action1, action2) denotes "action1 happens-before action2"
  // keep in mind that hb is transitive, i.e. hb(a, b) and hb(b, c) means hb(a, c)
  // all relations are set for the same bucket index and string id, meaning that
  // volatile write_bucket_1 does not imply hb(write_bucket_1, read_bucket_2)
  override def intern(fullRawString: Array[Byte], offset: Int, length: Int): Int = {
    val hash = hashOf(fullRawString, offset, length)
    var bucketIndex = hashToBucketIndex(hash)
    var cycles = 0
    while (true) {
      // volatile read, so hb(buckets_write, buckets_read)
      val bucket = buckets.get(bucketIndex) // buckets_read
      if (bucket == Empty) {
        // adding a new string to the mapping
        // as much work as possible is moved out of critical path
        // at the cost of possible work duplication
        val rawString = util.Arrays.copyOfRange(fullRawString, offset, offset + length)
        val newString = new String(rawString, StandardCharsets.UTF_8)
        // optimistic lock via CAS
        val setSucceeded = buckets.compareAndSet(bucketIndex, Empty, Locked)
        if (setSucceeded) {
          // atomic increment, single id is never shared between threads
          val newId = nextFreeId.getAndIncrement()
          keys.set(newId, rawString) // volatile keys_write
          strings.set(newId, newString) // volatile strings_write
          // by program order within a thread
          // hb(keys_write, buckets_write) and hb(strings_write, buckets_write)
          // this links bucketIndex from buckets and string id from keys and strings arrays
          buckets.set(bucketIndex, toBucketValue(newId, hash)) // volatile buckets_write
          return newId
        }
      } else if (bucket == Locked) {
        // someone is updating the bucket right now
        // should not happen very often, so a small delay due to descheduling is fine
        LockSupport.parkNanos(1)
      } else if (extractHash(bucket) == hash) {
        val id = extractKeyId(bucket)
        // we have hb(buckets_read, keys_read) between linked bucketIndex and stringId
        // by hb(keys_write, buckets_write), hb(buckets_write, buckets_read) and hb(buckets_read, keys_read)
        // we have hb(keys_write, keys_read)
        val stringOfBucket = keys.get(id) // keys_read
        if (isEquals(fullRawString, offset, length, stringOfBucket)) {
          return id
        } else {
          // same bucket index for same hash, but different keys
          bucketIndex = onCollisionNextIndex(bucketIndex)
        }
      } else {
        // same bucket index for different hashes
        bucketIndex = onCollisionNextIndex(bucketIndex)
      }
      cycles += 1
      infiniteLoopProtection(cycles, bucketIndex)
    }
    throw new IllegalStateException("Finished infinite loop?")
  }

  override def lookup(id: Int): String = {
    assert(id >= 0, s"Id must be non-negative, but got $id")
    // there are a theoretical race condition if we get free id after increment but before strings.set
    // should not be a problem as under normal circumstances we don't use string id until it has been returned
    // from intern method
    assert(
      id < nextFreeId.get(),
      s"Id must be in range of mapping, but got $id" +
        s" while total of ${nextFreeId.get()} strings were interned"
    )
    strings.get(id)
  }

  override def size(): Index = nextFreeId.get() - 1 // id 0 is not used, hence the substraction

  protected def hashOf(s: Utf8String, offset: Int, length: Int): Int = {
    var result = 1
    var i = offset
    while (i < offset + length) {
      result = 31 * result + s(i)
      i += 1
    }
    result
  }
  private def isEquals(s1: Utf8String, s1Offset: Int, s1Length: Int, s2: Utf8String): Boolean = {
    util.Arrays.equals(s1, s1Offset, s1Offset + s1Length, s2, 0, s2.length)
  }
  private def onCollisionNextIndex(nodeI: Index): Index = {
    val nextI = nodeI + 1
    if (nextI >= bucketCount) 0 else nextI
  }
  // bucket value is one long, 8 bytes
  // upper 4 bytes are id of string (or index of string and key in corresponding arrays)
  // lower 4 bytes are hash code of key for faster bucket matching
  private def toBucketValue(id: Int, hash: Hash): Bucket = {
    (id.toLong << 32) | java.lang.Integer.toUnsignedLong(hash)
  }
  private def extractHash(bucket: Bucket): Hash = {
    // drops upper 32 bits
    bucket.toInt
  }
  private def extractKeyId(bucket: Bucket): Int = {
    // drops lower 32 bits
    (bucket >> 32).toInt
  }

  {
    def isPowerOfTwo(number: Int): Boolean = number > 0 && ((number & (number - 1)) == 0)
    assert(isPowerOfTwo(bucketCount), s"Bucket count must be a power of two, but got $bucketCount")
  }
  private val mask = {
    // should be bit string like 000000111111111
    Integer.highestOneBit(bucketCount) - 1
  }
  // we should do hash % bucketCount
  // but knowing that bucketCount is power of two, we can use & to do the same but faster
  protected def hashToBucketIndex(hash: Int): Index = hash & mask

  // a bit of precaution to not stuck forever in case of whatever bug we might have
  // it's better to fail one measurement rather than lock aggregator thread forever
  private val MaxCycles = 10000
  private def infiniteLoopProtection(cycles: Int, nodeI: Index): Unit = {
    if (cycles >= MaxCycles) {
      throw new IllegalStateException(
        s"Intern map seems to be broken, iterating for $MaxCycles iterations" +
          s" with node index $nodeI and node value ${buckets.get(nodeI)}"
      )
    }
  }
}
