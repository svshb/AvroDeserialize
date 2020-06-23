package tsdb.deserialize.sort

import tsdb.BaseSpec

import scala.util.Random

class InsertionTagsSorterSpec extends BaseSpec {
  private val sort = InsertionTagsSorter
  "InsertionTagsSorter" must {
    "pass empty tags" in {
      val empty = new Array[Int](0)
      sort.sort(empty)
      assert(empty.isEmpty)
    }
    "reject malformed tags" in {
      val empty = new Array[Int](1)
      assertThrows[IllegalArgumentException](sort.sort(empty))
    }
    "correctly sort array" in {
      val r      = new Random(0)
      val tags   = r.shuffle(Range(0, 10).toList).flatMap(idx => Array(idx, r.nextInt(10))).toArray
      val sorted = tags.grouped(2).toArray.sortBy(_.head).flatten
      sort.sort(tags)
      assert(tags sameElements sorted)
    }
    "pass fuzz check" in {
      val r = new Random(0)
      Range(0, 1000).foreach(_ => {
        val tags   = r.shuffle(Range(0, r.nextInt(100)).toList).flatMap(idx => Array(idx, r.nextInt(100))).toArray
        val sorted = tags.grouped(2).toArray.sortBy(_.head).flatten
        sort.sort(tags)
        assert(tags sameElements sorted)
      })
    }
  }
}
