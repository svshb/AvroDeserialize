package tsdb.benchmark_04

import tsdb.BaseSpec
import tsdb.measurement.MeasurementsState

class ConcurrentDeserializeSpec extends BaseSpec {
  import collection.JavaConverters._
  "Concurrent deserialize spec" must {
    "deserialize measurement in benchmark" in {
      val benchmark = new ConcurrentDeserializeBenchmark()
      val measurementsState = new MeasurementsState()
      val deserializerState = new ConcurrentInternerDeserializerState()
      val internState = new ConcurrentStringInternerState
      deserializerState.setup(internState)
      val measurement = benchmark.deserializeConcurrentInterner(measurementsState, deserializerState)
      assert(measurement.logtime == measurementsState.measurement.getLogtime)
      val measurementTags = measurement.tags.map(internState.interner.lookup).toSet
      val expectedTags = measurementsState.measurement.getTags.asScala.flatMap {
        case (tk, tv) => Seq(tk, tv)
      }.toSet
      assert(measurementTags == expectedTags)
      assert(internState.interner.lookup(measurement.metric) == measurementsState.measurement.getMetric)
      assert(measurement.value == measurementsState.measurement.getValue)
    }
  }
}
