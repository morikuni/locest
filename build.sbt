lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7",
  resolvers ++= Seq(
    "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    "Atilika Open Source repository" at "http://www.atilika.org/nexus/content/repositories/atilika"
  )
)

lazy val root =
  (project in file("."))
    .aggregate(util, area, frequency)

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
        "com.typesafe" % "config" % "1.3.0",
        "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
        specs2 % "test"
      )
    )
    .dependsOn(util)

lazy val frequency =
  (project in file("frequency"))
    .enablePlugins(play.PlayScala)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        "org.scalikejdbc" %% "scalikejdbc" % "2.3.0",
        "com.typesafe" % "config" % "1.3.0",
        "org.postgresql" % "postgresql" % "9.4-1206-jdbc42",
        "org.atilika.kuromoji" % "kuromoji" % "0.7.7",
        ws,
        specs2 % "test"
      )
    )
    .dependsOn(util)
