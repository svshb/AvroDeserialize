package tsdb.benchmark_02

import tsdb.deserialize.ManualAvroDeserializer
import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Thread)
class ManualDeserializeState {
  val deserializer = new ManualAvroDeserializer()
}
