package tsdb.deserialize

import java.util

import tsdb.deserialize.sort.{InsertionTagsSorter, TagsSorter}
import tsdb.interner.StringInterner
import tsdb.measurement.MeasurementV2
import jdk.internal.vm.annotation.Contended

@Contended
class InterningAvroDeserializerArrayTags(val interner: StringInterner,
                                         protected val sorter: TagsSorter = InsertionTagsSorter)
    extends BaseInterningAvroDeserializer {
  def deserialize(bytes: Array[Byte]): MeasurementV2 = {
    init(bytes)
    val metric = readString()
    val tags = readTags()
    val logtime = readLong()
    val value = readLong()

    MeasurementV2(metric, tags, logtime, value)
  }

  private def readTags(): Array[Int] = {
    var blockSize: Int = readInt()
    var tags = new Array[Int](blockSize * 2)
    var idx = 0
    while (blockSize != 0) {
      while (blockSize != 0) {
        val tk = readString()
        val tv = readString()
        tags(idx) = tk
        idx += 1
        tags(idx) = tv
        idx += 1
        blockSize -= 1
      }
      blockSize = readInt()
      if (blockSize != 0) {
        tags = util.Arrays.copyOf(tags, tags.length + blockSize * 2)
      }
    }
    sorter.sort(tags)
    tags
  }
}
