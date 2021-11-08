package tsdb.deserialize

import tsdb.BaseSpec
import tsdb.measurement.MeasurementsState

class ManualAvroDeserializerSpec extends BaseSpec {
  "ManualAvroDeserializerSpec" must {
    "deserialize a sample payload" in {
      val measurementState = new MeasurementsState()
      val deserializer = new ManualAvroDeserializer()
      val deserializedMeasurement = deserializer.deserialize(measurementState.measurementBytes)
      assert(deserializedMeasurement.getLogtime == measurementState.measurement.getLogtime)
      assert(deserializedMeasurement.getMetric == measurementState.measurement.getMetric)
      assert(deserializedMeasurement.getTags == measurementState.measurement.getTags)
      assert(deserializedMeasurement.getValue == measurementState.measurement.getValue)
    }
  }
}
