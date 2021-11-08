package tsdb.deserialize

import tsdb.BaseSpec
import tsdb.interner.StringInternerImpl
import tsdb.measurement.MeasurementsState

import scala.collection.JavaConverters._

class InterningAvroDeserializerHashMapTagsSpec extends BaseSpec {
  "InterningAvroDeserializerHashMapTags" must {
    "deserialize a sample payload" in {
      val measurementState = new MeasurementsState()
      val interner = new StringInternerImpl()
      val deserializer = new InterningAvroDeserializerHashMapTags(interner)
      val deserializedMeasurement = deserializer.deserialize(measurementState.measurementBytes)
      assert(deserializedMeasurement.logtime == measurementState.measurement.getLogtime)
      assert(interner.lookup(deserializedMeasurement.metric) == measurementState.measurement.getMetric)
      assert(deserializedMeasurement.value == measurementState.measurement.getValue)
      val deserializedTags = deserializedMeasurement.tags.asScala.map {
        case (key, value) => interner.lookup(key) -> interner.lookup(value)
      }
      assert(deserializedTags == measurementState.measurement.getTags.asScala)
    }
  }
}
