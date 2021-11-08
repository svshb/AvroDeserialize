package tsdb.deserialize

import java.nio.charset.StandardCharsets

import tsdb.BaseSpec
import tsdb.deserialize.sort.NoOpTagsSorter
import tsdb.interner.StringInternerImpl
import tsdb.measurement.MeasurementsState

import scala.collection.JavaConverters._

class InterningAvroDeserializerArrayTagsSpec extends BaseSpec {
  "InterningAvroDeserializerArrayTags" must {
    "deserialize a sample payload" in {
      val measurementState = new MeasurementsState()
      val interner = new StringInternerImpl()
      val deserializer = new InterningAvroDeserializerArrayTags(interner)
      val deserializedMeasurement = deserializer.deserialize(measurementState.measurementBytes)
      assert(deserializedMeasurement.logtime == measurementState.measurement.getLogtime)
      assert(interner.lookup(deserializedMeasurement.metric) == measurementState.measurement.getMetric)
      assert(deserializedMeasurement.value == measurementState.measurement.getValue)
      val deserializedTags = deserializedMeasurement.tags
        .map(interner.lookup)
        .grouped(2)
        .map {
          case Array(key, value) => key -> value
        }
        .toMap
      assert(deserializedTags == measurementState.measurement.getTags.asScala)
    }
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
