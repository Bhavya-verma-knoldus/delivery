ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "delivery"
  ).enablePlugins(PlayScala)

val jacksonVersion         = "2.13.4"   // or 2.12.7
val jacksonDatabindVersion = "2.13.4.2" // or 2.12.7.1

val jacksonOverrides = Seq(
  "com.fasterxml.jackson.core"     % "jackson-core",
  "com.fasterxml.jackson.core"     % "jackson-annotations",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310"
).map(_ % jacksonVersion)

val jacksonDatabindOverrides = Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonDatabindVersion
)

val akkaSerializationJacksonOverrides = Seq(
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor",
  "com.fasterxml.jackson.module"     % "jackson-module-parameter-names",
  "com.fasterxml.jackson.module"     %% "jackson-module-scala",
).map(_ % jacksonVersion)

libraryDependencies ++= jacksonDatabindOverrides ++ jacksonOverrides ++ akkaSerializationJacksonOverrides

libraryDependencies ++= Seq( jdbc )

libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.6"

libraryDependencies ++= Seq(
  guice,
  ws,
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.typesafe.play" %% "play-json-joda" % "2.10.1",
  "com.typesafe.play" %% "play-server" % "2.8.20",
  "com.amazonaws" % "amazon-kinesis-client" % "1.14.10",
  "software.amazon.kinesis" % "amazon-kinesis-client" % "2.4.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "org.slf4j" % "slf4j-api" % "2.0.6",
  "ch.qos.logback" % "logback-classic" % "1.4.7",
 "joda-time" % "joda-time" % "2.12.5",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2",
  "org.postgresql" % "postgresql" % "42.6.0",
)

// https://mvnrepository.com/artifact/software.amazon.awssdk/aws-json-protocol
libraryDependencies += "software.amazon.awssdk" % "aws-json-protocol" % "2.21.24"
libraryDependencies += "software.amazon.awssdk" % "kinesis" % "2.20.26"

dependencyOverrides ++= Seq(
  "com.google.inject" % "guice" % "5.1.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0")
