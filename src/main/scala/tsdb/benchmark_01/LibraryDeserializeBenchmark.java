package tsdb.benchmark_01;

import tsdb.measurement.MeasurementAvro;
import tsdb.measurement.MeasurementsState;
import org.apache.avro.io.DecoderFactory;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.IOException;

/**
 * Baseline - the Avro library deserialize.
 */
public class LibraryDeserializeBenchmark {
    @Benchmark
    public MeasurementAvro deserialize(MeasurementsState measurements, LibraryDeserializeState state) throws IOException {
        var decoder = DecoderFactory.get().binaryDecoder(measurements.measurementBytes(), state.decoder());
        return state.reader().read(null, decoder);
    }
}
