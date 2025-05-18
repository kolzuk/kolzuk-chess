ThisBuild / organization := "ru.bagardynov"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.15"

val scalaTestVersion = "3.2.19"
val catsVersion      = "2.8.0"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.15",
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test
  )
)

lazy val chessEngine = (project in file("modules/chess-engine"))
  .settings(
    name := "chess-engine",
    commonSettings,
  ).dependsOn(common)

lazy val publicApi = (project in file("modules/public-api"))
  .settings(
    name := "public-api",
    commonSettings
  )

lazy val common = (project in file("modules/common"))
  .settings(
    name := "common",
    commonSettings
  )