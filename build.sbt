name := "locest"

version := "0.1-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7"
)

lazy val root =
  (project in file("."))
    .settings(commonSettings)
    .aggregate(util)

lazy val util =
  (project in file("util"))
    .settings(commonSettings)