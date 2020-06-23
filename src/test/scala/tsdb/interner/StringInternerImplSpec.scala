package tsdb.interner

class StringInternerImplSpec extends CommonInternerSpecs {
  override protected def createInterner(): StringInterner = new StringInternerImpl
}
