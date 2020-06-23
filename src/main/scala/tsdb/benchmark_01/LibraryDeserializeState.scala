package tsdb.benchmark_01

import tsdb.measurement.MeasurementAvro
import org.apache.avro.io.{BinaryDecoder, DecoderFactory}
import org.apache.avro.specific.{SpecificData, SpecificDatumReader}
import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Thread)
class LibraryDeserializeState {
  val decoder: BinaryDecoder = DecoderFactory.get.binaryDecoder(Array.empty[Byte], null)
  val reader = new SpecificDatumReader[MeasurementAvro](
    MeasurementAvro.SCHEMA$,
    MeasurementAvro.SCHEMA$,
    SpecificData.get
  )
}
