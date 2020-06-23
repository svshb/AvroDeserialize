package tsdb.benchmark_03

import tsdb.deserialize.InterningAvroDeserializerArrayTags
import tsdb.interner.StringInternerImpl
import org.openjdk.jmh.annotations.{Scope, State}

@State(Scope.Thread)
class ArrayTagsInternDeserializeState {
  val interner = new StringInternerImpl
  val deserializer = new InterningAvroDeserializerArrayTags(interner)
}
