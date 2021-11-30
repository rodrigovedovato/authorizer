import Dependencies._

ThisBuild / scalaVersion     := "2.13.7"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.nubank"
ThisBuild / organizationName := "nubank"

lazy val root = (project in file("."))
  .settings(
    name := "authorizer",
    libraryDependencies ++= Seq(
        "org.apache.commons" % "commons-lang3" % "3.12.0",
        scalaTest % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
