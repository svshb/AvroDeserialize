package tsdb

import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.{OptionsBuilder, TimeValue}

/**
 * Main for brave souls who can remember to do `sbt compile` every time before running this class.
 * More convenient version of calling JMH.
 */
object Main {
  def main(args: Array[String]): Unit = {
    val options = new OptionsBuilder()
      .forks(10)
      .threads(4)
      .shouldFailOnError(true)
      .warmupIterations(10)
      .measurementIterations(30)
      .measurementTime(TimeValue.seconds(1))
      .warmupTime(TimeValue.seconds(1))
      .include(".*TestBench.*")
      .build()
    new Runner(options).run()
  }
}
