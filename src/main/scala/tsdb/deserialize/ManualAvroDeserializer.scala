package tsdb.deserialize

import java.nio.charset.StandardCharsets
import java.util

import tsdb.measurement.MeasurementAvro
import jdk.internal.vm.annotation.Contended

// https://avro.apache.org/docs/1.9.2/spec.html#binary_encoding
@Contended
class ManualAvroDeserializer extends BaseAvroDeserializer {
  def deserialize(bytes: Array[Byte]): MeasurementAvro = {
    init(bytes)
    val metric = readString()
    val tags = readTags()
    val logtime = readLong()
    val value = readLong()

    new MeasurementAvro(metric, tags, logtime, value)
  }

  private def readString(): String = {
    val size = readInt()
    val value = new String(util.Arrays.copyOfRange(buf, pos, pos + size), StandardCharsets.UTF_8)
    pos += size
    value
  }

  private def readTags(): util.Map[CharSequence, CharSequence] = {
    var blockSize: Int = readInt()
    val tags = new util.HashMap[CharSequence, CharSequence]()
    while (blockSize != 0) {
      Range(0, blockSize).foreach(_ => {
        val tk = readString()
        val tv = readString()
        tags.put(tk, tv)
      })
      blockSize = readInt()
    }
    tags
  }
}
