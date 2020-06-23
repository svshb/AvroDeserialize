package tsdb.benchmark_03;

import tsdb.measurement.MeasurementV1;
import tsdb.measurement.MeasurementV2;
import tsdb.measurement.MeasurementsState;
import org.openjdk.jmh.annotations.Benchmark;

/**
 * Introducing string interner to manual parse.
 */
public class InternDeserializeBenchmark {
    /**
     * This method uses measurement model with Map[Int, Int] tags.
     */
    @Benchmark
    public MeasurementV1 deserialize01HashMapTags(MeasurementsState measurements, StringInternDeserializeState state) {
        return state.deserializer().deserialize(measurements.measurementBytes());
    }

    /**
     * This method uses measurement model with Array[Int] tags.
     */
    @Benchmark
    public MeasurementV2 deserialize02ArrayTags(MeasurementsState measurements, ArrayTagsInternDeserializeState state) {
        return state.deserializer().deserialize(measurements.measurementBytes());
    }
}
