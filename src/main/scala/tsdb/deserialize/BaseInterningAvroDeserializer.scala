package tsdb.deserialize

import tsdb.interner.StringInterner

trait BaseInterningAvroDeserializer extends BaseAvroDeserializer {
  protected val interner: StringInterner

  protected def readString(): Int = {
    val size = readInt()
    val value = interner.intern(buf, pos, size)
    pos += size
    value
  }
}
