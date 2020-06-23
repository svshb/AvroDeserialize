package tsdb.benchmark_03

import tsdb.BaseSpec
import tsdb.measurement.MeasurementsState

class InternDeserializeSpec extends BaseSpec {
  import collection.JavaConverters._
  "deserialize measurement in old model benchmark" in {
    val benchmark = new InternDeserializeBenchmark()
    val measurementsState = new MeasurementsState()
    val internState = new StringInternDeserializeState
    val measurement = benchmark.deserialize01HashMapTags(measurementsState, internState)
    assert(measurement.logtime == measurementsState.measurement.getLogtime)
    val measurementTags = measurement.tags.asScala.map {
      case (tk, tv) => (internState.interner.lookup(tk), internState.interner.lookup(tv))
    }
    assert(measurementTags == measurementsState.measurement.getTags.asScala)
    assert(internState.interner.lookup(measurement.metric) == measurementsState.measurement.getMetric)
    assert(measurement.value == measurementsState.measurement.getValue)
  }
  "deserialize measurement in new model benchmark" in {
    val benchmark = new InternDeserializeBenchmark()
    val measurementsState = new MeasurementsState()
    val internState = new ArrayTagsInternDeserializeState
    val measurement = benchmark.deserialize02ArrayTags(measurementsState, internState)
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
