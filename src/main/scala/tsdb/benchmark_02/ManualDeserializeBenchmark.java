package tsdb.benchmark_02;

import tsdb.measurement.MeasurementAvro;
import tsdb.measurement.MeasurementsState;
import org.openjdk.jmh.annotations.Benchmark;

/**
 * Manual parse of Avro message.
 */
public class ManualDeserializeBenchmark {
    @Benchmark
    public MeasurementAvro deserialize(MeasurementsState measurements, ManualDeserializeState state) {
        return state.deserializer().deserialize(measurements.measurementBytes());
    }
}
