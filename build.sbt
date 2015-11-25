name := "locest"

version := "0.1-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7"
)

lazy val root =
  (project in file("."))
    .settings(commonSettings)
    .aggregate(area, util)

lazy val util =
  (project in file("util"))
    .settings(commonSettings)

lazy val area =
  (project in file("area"))
    .enablePlugins(play.PlayScala)
    .settings(commonSettings)
    .dependsOn(util)
