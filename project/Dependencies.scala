import sbt._

object Dependencies {

  private object Versions {
    val apacheAvroVersion = "1.9.2"
    val jmhVersion = "1.19"
    val slf4jVersion = "1.7.30"
    val scalatestVersion = "3.1.1"
  }

  import Versions._

  private val apacheAvroDependencies: Seq[ModuleID] = Seq(
    "org.apache.avro" % "avro" % apacheAvroVersion
  )

  private val jmhDependencies: Seq[ModuleID] = Seq(
    "org.openjdk.jmh" % "jmh-core" % jmhVersion,
    "org.openjdk.jmh" % "jmh-generator-annprocess" % jmhVersion
  )

  private val loggingDependencies: Seq[ModuleID] = Seq(
     "org.slf4j" % "slf4j-simple" % slf4jVersion
  )

  private val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % Test
  )

  val dependencies: Seq[ModuleID] = apacheAvroDependencies ++
    jmhDependencies ++
    loggingDependencies ++
    testDependencies
}
