package tsdb.deserialize.sort

trait TagsSorter {
  def sort(tags: Array[Int]): Unit
}

object NoOpTagsSorter extends TagsSorter {
  override def sort(tags: Array[Int]): Unit = {}
}

// https://en.wikipedia.org/wiki/Insertion_sort
object InsertionTagsSorter extends TagsSorter {
  override def sort(tags: Array[Int]): Unit = {
    if (tags.length % 2 != 0) {
      throw new IllegalArgumentException(s"Malformed tags, expected even size, but got ${tags.length}")
    }
    var i = 2
    while (i < tags.length) {
      var j = i
      while (j > 0 && tags(j - 2) > tags(j)) {
        val tmpTk = tags(j)
        val tmpTv = tags(j + 1)
        tags(j) = tags(j - 2)
        tags(j + 1) = tags(j - 2 + 1)
        tags(j - 2) = tmpTk
        tags(j - 2 + 1) = tmpTv
        j -= 2
      }
      i += 2
    }
  }
}
