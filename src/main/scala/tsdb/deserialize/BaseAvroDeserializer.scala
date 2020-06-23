package tsdb.deserialize

trait BaseAvroDeserializer {
  protected var buf: Array[Byte] = _
  protected var pos = 0

  protected def init(bytes: Array[Byte]): Unit = {
    buf = bytes
    pos = 0
  }

  protected def readLong(): Long = {
    var r: Long = 0L
    var i = 0
    var value = 0L
    do {
      value = buf(pos)
      r = r ^ ((value & 0x7F) << (i * 7))
      i += 1
      pos += 1
    } while ((value & 0x80) != 0)
    (r >>> 1) ^ -(r & 1)
  }

  protected def readInt(): Int = {
    var r = 0
    var i = 0
    var value = 0
    do {
      value = buf(pos)
      r = r ^ ((value & 0x7F) << (i * 7))
      i += 1
      pos += 1
    } while ((value & 0x80) != 0)
    (r >>> 1) ^ -(r & 1)
  }
}
