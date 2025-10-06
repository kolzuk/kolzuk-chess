ThisBuild / organization := "ru.bagardynov"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.15"

val scalaTestVersion = "3.2.19"
val catsVersion      = "2.8.0"
val zioVersion       = "2.1.6"
val zioHttpVersion   = "3.0.0"
val zioJsonVersion   = "0.7.3"

val commonLibs = Seq(
  // ZIO
  "dev.zio"       %% "zio"       % zioVersion,
  "dev.zio"       %% "zio-http"  % zioHttpVersion,
  "dev.zio"       %% "zio-json"  % zioJsonVersion,

  // Cats
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
)

lazy val commonSettings = Seq(
  scalaVersion := "2.13.15",
  libraryDependencies ++= commonLibs
)

import sbtassembly.AssemblyPlugin.autoImport.*

lazy val chessEngine = (project in file("modules/chess-engine"))
  .settings(
    name := "chess-engine",
    commonSettings,
    Compile / mainClass := Some("chessengine.Main"),
    assembly / assemblyMergeStrategy := {
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case PathList("META-INF", xs @ _*)           => MergeStrategy.discard
      case _                                       => MergeStrategy.first
    }
  ).dependsOn(common)

lazy val publicApi = (project in file("modules/public-api"))
  .settings(
    name := "public-api",
    commonSettings,
  )

lazy val common = (project in file("modules/common"))
  .settings(
    name := "common",
    commonSettings
  )