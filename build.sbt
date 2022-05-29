ThisBuild / organizationName := "com.tistory.hskimsky"
ThisBuild / name := "spark-app-example"
ThisBuild / version := "1.0.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13.2" % Test
  , "ch.qos.logback" % "logback-classic" % "1.2.11" % Test

  // , "com.fasterxml.jackson.core" % "jackson-core" % "2.10.5"
  // , "com.fasterxml.jackson.core" % "jackson-annotations" % "2.10.5"
  // , "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.5"
  // , "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.5"

  , "org.apache.spark" %% "spark-core" % "2.4.7" % Provided
  , "org.apache.spark" %% "spark-sql" % "2.4.7" % Provided

  , "org.apache.hadoop" % "hadoop-client" % "3.1.1" % Provided
  , "org.apache.hadoop" % "hadoop-aws" % "3.1.1" % Provided
)

lazy val root = (project in file(".")).settings(
  name := "spark-app-example"
  , idePackagePrefix := Some("com.tistory.hskimsky")
  , assembly / mainClass := Some("com.tistory.hskimsky.ExampleJob")
  , assembly / assemblyJarName := "spark-app-example.jar"
  , assembly / assemblyMergeStrategy := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
  , assembly / assemblyOption ~= {
    _.withIncludeScala(false)
      .withIncludeDependency(true)
  }
)
