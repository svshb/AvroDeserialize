package tsdb.interner.concurrent

import java.nio.charset.StandardCharsets
import java.util.concurrent.{CountDownLatch, TimeUnit}

import tsdb.interner.{CommonInternerSpecs, ConcurrentStringInterner, StringInterner}

import scala.util.Random

class ConcurrentStringInternerSpec extends CommonInternerSpecs {
  override protected def createInterner(): StringInterner = new ConcurrentStringInterner(1 << 8)
  private val ConcurrentTestIterations = 100
  "ConcurrentStringInterner" must {
    "[potentially flaky] handle concurrency in intern on the same key" in {
      Range(0, ConcurrentTestIterations).foreach(iteration => {
        val threadsCount = math.min(8, Runtime.getRuntime.availableProcessors())
        // we use cdl to sync all threads to maximize contention over the key
        val internStartCdl = new CountDownLatch(threadsCount)
        val internFinishCdl = new CountDownLatch(threadsCount)
        val interner = new ConcurrentStringInterner(1024)
        // random is seeded for perfect reproducibility
        val string = new Random(iteration).nextString(10)
        val rawString = string.getBytes(StandardCharsets.UTF_8)
        Range(0, threadsCount).foreach(i => {
          val t = new Thread(() => {
            internStartCdl.countDown()
            internStartCdl.await(1, TimeUnit.MINUTES)
            interner.intern(rawString, 0, rawString.length)
            internFinishCdl.countDown()
          })
          t.setName(s"ConcurrentStringInternerSpec-$i")
          t.start()
        })
        internFinishCdl.await(1, TimeUnit.MINUTES)
        assert(interner.size() == 1)
        assert(interner.lookup(1) == string)
      })
    }
  }
}
