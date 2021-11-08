package tsdb.interner

/**
  * String interner allows to map raw utf-8 bytes to numeric id, and numeric id back to String object.
  */
trait StringInterner {

  /**
    * Interns bytes chunk treating it as utf-8 bytes
    * @param bytes array to get utf-8 bytes chunk from
    * @param offset offset of chunk
    * @param length length of chunk
    * @return either new id if chunk was never interned, or result of first intern of this byte chunk
    */
  def intern(bytes: Array[Byte], offset: Int, length: Int): Int

  /**
    * Returns String object by id
    * @param id id of string
    * @return String if it was interned before, or null
    */
  def lookup(id: Int): String

  /**
    * Returns the size of string interner.
    * @return amount of strings stored in this interner
    */
  def size(): Int
}
