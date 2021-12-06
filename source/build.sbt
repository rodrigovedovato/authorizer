import Dependencies._

ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.nubank"
ThisBuild / organizationName := "nubank"

enablePlugins(JavaAppPackaging, JlinkPlugin)

lazy val root = (project in file("."))
  .settings(
    name := "authorizer",
    libraryDependencies ++= Seq(
      circeCore,
      circeGeneric,
      circeParser,
      circeGenericExtras,
      commonsLang,
      scalaTest % Test
    ),
    scalacOptions ++= Seq(
      "-Ymacro-annotations"
    ),

  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
