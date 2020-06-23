package tsdb.deserialize

import java.nio.charset.StandardCharsets

import tsdb.BaseSpec
import tsdb.deserialize.sort.NoOpTagsSorter
import tsdb.interner.StringInternerImpl
import tsdb.measurement.MeasurementsState

class InterningAvroDeserializeV2Spec extends BaseSpec {
  "InterningAvroDeserializeV2" must {
    "sort tags" in {
      val interner = new StringInternerImpl()
      val sortDeserializer = new InterningAvroDeserializerArrayTags(interner)
      val noSortDeserializer = new InterningAvroDeserializerArrayTags(interner, NoOpTagsSorter)
      val measurement = new MeasurementsState().measurementBytes
      // we need to intern this beforehand so that tags are in different order
      interner.intern("tag3".getBytes(StandardCharsets.UTF_8), 0, 4)
      val sortedResult = sortDeserializer.deserialize(measurement)
      val notSortedResult = noSortDeserializer.deserialize(measurement)
      assert(!(sortedResult.tags sameElements notSortedResult.tags))
      val manuallySortedTags = notSortedResult.tags.grouped(2).toSeq.sortBy(_.head).flatten
      assert(sortedResult.tags sameElements manuallySortedTags)
    }
  }
}
