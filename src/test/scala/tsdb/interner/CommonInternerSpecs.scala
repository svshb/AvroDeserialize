package tsdb.interner

import java.nio.charset.StandardCharsets

import tsdb.BaseSpec

abstract class CommonInternerSpecs extends BaseSpec {
  protected def createInterner(): StringInterner
  this.getClass.getSimpleName must {
    "intern string and lookup afterwards" in {
      val interner = createInterner()
      val strings = Range(0, 5).map(_.toString)
      val ids = strings.map(str => {
        val bytes = str.getBytes(StandardCharsets.UTF_8)
        interner.intern(bytes, 0, bytes.length)
      })
      val internedStrings = ids.map(interner.lookup)
      assert(internedStrings == strings)
      assert(interner.size() == 5)
    }
    "use slice of string" in {
      val interner = createInterner()
      val string = "asd"
      val bytes = string.getBytes(StandardCharsets.UTF_8)
      val id = interner.intern(bytes, 1, 1)
      assert(interner.lookup(id) == "s")
    }
  }
}
