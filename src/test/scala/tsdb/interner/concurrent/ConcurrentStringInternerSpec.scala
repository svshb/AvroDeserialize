package tsdb.interner.concurrent

import tsdb.interner.{CommonInternerSpecs, ConcurrentStringInterner, StringInterner}

class ConcurrentStringInternerSpec extends CommonInternerSpecs {
  override protected def createInterner(): StringInterner = new ConcurrentStringInterner(1 << 8)
}
