package tsdb.benchmark_04

import tsdb.deserialize.InterningAvroDeserializerArrayTags
import org.openjdk.jmh.annotations.{Level, Scope, Setup, State}
import tsdb.interner.ConcurrentStringInterner

@State(Scope.Thread)
class ConcurrentInternerDeserializerState {
  var deserializer: InterningAvroDeserializerArrayTags = null
  @Setup(Level.Trial)
  def setup(internerState: ConcurrentStringInternerState): Unit = {
    deserializer = new InterningAvroDeserializerArrayTags(internerState.interner)
  }
}

@State(Scope.Benchmark)
class ConcurrentStringInternerState {
  val interner = new ConcurrentStringInterner(1 << 8)
}
