package tsdb

import tsdb.benchmark_01.{LibraryDeserializeBenchmark, LibraryDeserializeState}
import tsdb.benchmark_02.{ManualDeserializeBenchmark, ManualDeserializeState}
import tsdb.benchmark_03.{StringInternDeserializeState, InternDeserializeBenchmark, ArrayTagsInternDeserializeState}
import tsdb.benchmark_04.{
  ConcurrentDeserializeBenchmark,
  ConcurrentInternerDeserializerState,
  ConcurrentStringInternerState
}
import tsdb.measurement.MeasurementsState

/**
 * Helper class to run async-profiler to analyze different benchmarks.
 * Assuming async-profiler is in `./async-profiler` following command can be used
 * to get a flamegraph of currently running method in `./flamegraph.svg`:
 * `./async-profiler/profiler.sh -d 10 -f ./flamegraph.svg $(jps | grep "AsyncProfilerHelperMain" | cut -d" " -f1)`
 * It might be required to run
 *
 * sudo su
 * echo 1 > /proc/sys/kernel/perf_event_paranoid
 * echo 0 > /proc/sys/kernel/kptr_restrict
 *
 * to make it work under Linux.
 */
object AsyncProfilerHelperMain {
  def main(args: Array[String]): Unit = {
    loop1Library()
  }

  private def loop1Library(): Unit = {
    val measurementsState = new MeasurementsState
    val state = new LibraryDeserializeState
    val benchmark = new LibraryDeserializeBenchmark()
    while (true) {
      benchmark.deserialize(measurementsState, state)
    }
  }

  private def loop2Manual(): Unit = {
    val measurementsState = new MeasurementsState
    val state = new ManualDeserializeState
    val benchmark = new ManualDeserializeBenchmark()
    while (true) {
      benchmark.deserialize(measurementsState, state)
    }
  }

  private def loop3Intern(): Unit = {
    val measurementsState = new MeasurementsState
    val state = new StringInternDeserializeState
    val benchmark = new InternDeserializeBenchmark()
    while (true) {
      benchmark.deserialize01HashMapTags(measurementsState, state)
    }
  }

  private def loop3InternV2(): Unit = {
    val measurementsState = new MeasurementsState
    val state = new ArrayTagsInternDeserializeState
    val benchmark = new InternDeserializeBenchmark()
    while (true) {
      benchmark.deserialize02ArrayTags(measurementsState, state)
    }
  }

  private def loop4Concurrent(): Unit = {
    val measurementsState = new MeasurementsState
    val deserializeState = new ConcurrentInternerDeserializerState
    val internerState = new ConcurrentStringInternerState
    deserializeState.setup(internerState)
    val benchmark = new ConcurrentDeserializeBenchmark()
    while (true) {
      benchmark.deserializeConcurrentInterner(measurementsState, deserializeState)
    }
  }
}
