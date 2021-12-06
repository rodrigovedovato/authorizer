import sbt._

object Dependencies {
  object versions {
    val circe = "0.14.1"
  }

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"
  lazy val circeCore = "io.circe" %% "circe-core" % versions.circe
  lazy val circeGeneric = "io.circe" %% "circe-generic" % versions.circe
  lazy val circeGenericExtras = "io.circe" %% "circe-generic-extras" % versions.circe
  lazy val circeParser = "io.circe" %% "circe-parser" % versions.circe

  lazy val commonsLang = "org.apache.commons" % "commons-lang3" % "3.12.0"
}
