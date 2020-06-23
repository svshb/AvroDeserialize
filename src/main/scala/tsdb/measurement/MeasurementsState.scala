package tsdb.measurement

import java.io.ByteArrayOutputStream
import java.time.Instant
import java.util

import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.SpecificDatumWriter
import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Benchmark)
class MeasurementsState {
  val measurement: MeasurementAvro = {
    val tags = new util.HashMap[CharSequence, CharSequence]()
    tags.put("tag1", "value1")
    tags.put("tag2", "value2")
    tags.put("tag3", "value3")
    tags.put("tag4", "value4")
    tags.put("tag5", "value5")

    new MeasurementAvro("metric.name", tags, Instant.now.toEpochMilli, 1L)
  }

  val measurementBytes: Array[Byte] = {
    val datumWriter =
      new SpecificDatumWriter[MeasurementAvro](classOf[MeasurementAvro])
    val baos = new ByteArrayOutputStream()
    val encoder = EncoderFactory.get().binaryEncoder(baos, null)
    datumWriter.write(measurement, encoder)
    encoder.flush()
    baos.close()
    baos.toByteArray
  }
}
