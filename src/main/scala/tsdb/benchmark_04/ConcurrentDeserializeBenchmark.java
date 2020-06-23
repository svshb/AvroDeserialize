package tsdb.benchmark_04;

import tsdb.measurement.MeasurementV2;
import tsdb.measurement.MeasurementsState;
import org.openjdk.jmh.annotations.Benchmark;

/**
 * Manual parse with string interning, where one concurrent-safe string interner is shared between 4 threads
 */
public class ConcurrentDeserializeBenchmark {
    @Benchmark
    public MeasurementV2 deserializeConcurrentInterner(MeasurementsState measurements, ConcurrentInternerDeserializerState state) {
        return state.deserializer().deserialize(measurements.measurementBytes());
    }
}
