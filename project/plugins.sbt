addSbtPlugin("org.jetbrains.scala" % "sbt-ide-settings" % "1.1.1")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.20")

// Resolve scala-xml version dependency mismatch, see https://github.com/sbt/sbt/issues/7007
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
