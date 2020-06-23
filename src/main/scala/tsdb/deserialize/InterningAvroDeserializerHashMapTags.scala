package tsdb.deserialize

import java.util

import tsdb.interner.StringInterner
import tsdb.measurement.MeasurementV1
import jdk.internal.vm.annotation.Contended

@Contended
class InterningAvroDeserializerHashMapTags(protected val interner: StringInterner) extends BaseInterningAvroDeserializer {
  def deserialize(bytes: Array[Byte]): MeasurementV1 = {
    init(bytes)
    val metric = readString()
    val tags = readTags()
    val logtime = readLong()
    val value = readLong()

    MeasurementV1(metric, tags, logtime, value)
  }

  private def readTags(): util.Map[Int, Int] = {
    var blockSize: Int = readInt()
    val tags = new util.HashMap[Int, Int]()
    while (blockSize != 0) {
      while (blockSize != 0) {
        val tk = readString()
        val tv = readString()
        tags.put(tk, tv)
        blockSize -= 1
      }
      blockSize = readInt()
    }
    tags
  }
}
