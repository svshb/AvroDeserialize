package tsdb.benchmark_03

import tsdb.deserialize.InterningAvroDeserializerHashMapTags
import tsdb.interner.StringInternerImpl
import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Thread)
class StringInternDeserializeState {
  val interner = new StringInternerImpl
  val deserializer = new InterningAvroDeserializerHashMapTags(interner)
}
