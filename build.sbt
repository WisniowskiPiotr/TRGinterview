name := "TRGInterview"
organization := "io.wistronics"
scalaVersion := "2.11.12"
libraryDependencies ++= Seq(
  compilerPlugin(scalafixSemanticdb),
  "dev.zio" %% "zio" % "1.0.9",
  "dev.zio" %% "zio-logging-slf4j" % "0.5.14",
  "org.apache.spark" %% "spark-core" % "2.4.8",
  "org.apache.spark" %% "spark-hive" % "2.4.8"
)
semanticdbEnabled := true
scalacOptions ++= Seq(
  "-Yrangepos", // required by SemanticDB compiler plugin
  "-Ywarn-unused-import" // required by `RemoveUnused` rule
)
javaOptions ++= Seq(
  "-Dderby.stream.error.file=derby.log",
  "-Dderby.system.home=./target"
)
run / fork := true

addCommandAlias("fmt", "; compile:scalafmt; test:scalafmt; scalafmtSbt; compile:scalafix; test:scalafix;")
