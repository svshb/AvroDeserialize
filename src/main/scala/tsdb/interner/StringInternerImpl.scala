package tsdb.interner

import java.nio.charset.StandardCharsets
import java.util

class StringInternerImpl extends StringInterner {
  private val mapping = new util.HashMap[Utf8, java.lang.Integer]()
  private val reverseMapping = new util.ArrayList[String]()
  private var nextFreeId = 0

  override def intern(bytes: Array[Byte], offset: Int, length: Int): Int = {
    val utf8 = new Utf8(util.Arrays.copyOfRange(bytes, offset, offset + length))
    var id: java.lang.Integer = mapping.get(utf8)
    if (id == null) {
      id = nextFreeId
      nextFreeId += 1
      mapping.put(utf8, id)
      reverseMapping.add(new String(utf8.bytes, StandardCharsets.UTF_8))
    }
    id
  }

  override def lookup(id: Int): String = {
    assert(id >= 0, s"Id must be non-negative, but got $id")
    assert(
      id < reverseMapping.size(),
      s"Id must be in range of mapping, but got $id" +
        s" while total of ${reverseMapping.size()} strings were interned"
    )
    reverseMapping.get(id)
  }

  override def size(): Int = reverseMapping.size()
}

class Utf8(val bytes: Array[Byte]) {
  override val hashCode: Int = util.Arrays.hashCode(bytes)

  override def equals(obj: Any): Boolean = {
    obj match {
      case other: Utf8 => util.Arrays.equals(this.bytes, other.bytes)
      case _           => false
    }
  }
}
