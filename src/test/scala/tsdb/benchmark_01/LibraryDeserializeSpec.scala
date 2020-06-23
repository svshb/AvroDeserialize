package tsdb.benchmark_01

import tsdb.BaseSpec
import tsdb.measurement.MeasurementsState

class LibraryDeserializeSpec extends BaseSpec {
  import collection.JavaConverters._
  "Library deserialize benchmark" must {
    "deserialize measurement in benchmark loop" in {
      val benchmark = new LibraryDeserializeBenchmark()
      val measurementsState = new MeasurementsState()
      val measurement =
        benchmark.deserialize(measurementsState, new LibraryDeserializeState)
      assert(measurement.getLogtime == measurementsState.measurement.getLogtime)
      assert(measurement.getMetric.toString == measurementsState.measurement.getMetric.toString)
      val measurementTags = measurement.getTags.asScala.map {
        case (tk, tv) => tk.toString -> tv.toString
      }
      val expectedTags = measurementsState.measurement.getTags.asScala.map {
        case (tk, tv) => tk.toString -> tv.toString
      }
      assert(measurementTags == expectedTags)
      assert(measurement.getValue == measurementsState.measurement.getValue)
    }
  }
}
