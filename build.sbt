name := "locest"

version := "0.1-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7"
)

lazy val root =
  (project in file("."))
    .aggregate(area, util)

lazy val util =
  (project in file("util"))
    .settings(commonSettings)

lazy val area =
  (project in file("area"))
    .enablePlugins(play.PlayScala)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalikejdbc" %% "scalikejdbc" % "2.3.0",
        "mysql" % "mysql-connector-java" % "5.1.37",
        "com.typesafe" % "config" % "1.3.0",
        "org.postgresql" % "postgresql" % "9.3-1104-jdbc41",
        specs2 % "test"
      )
    )
    .dependsOn(util)
