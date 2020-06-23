package tsdb.benchmark_02

import tsdb.BaseSpec
import tsdb.measurement.MeasurementsState

class ManualDeserializeSpec extends BaseSpec {
  "Manual deserialize benchmark" must {
    "deserialize measurement in benchmark loop" in {
      val benchmark = new ManualDeserializeBenchmark()
      val measurementsState = new MeasurementsState()
      val measurement =
        benchmark.deserialize(measurementsState, new ManualDeserializeState)
      assert(measurement == measurementsState.measurement)
    }
  }
}
