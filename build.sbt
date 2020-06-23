name := "AvroDeserialize"
version := "1.0"
scalaVersion := "2.12.10"
libraryDependencies ++= Dependencies.dependencies
javaOptions ++= Seq(
  // this is to prevent false sharing over Deserializer objects
  "-XX:-RestrictContended"
)
enablePlugins(JmhPlugin)
