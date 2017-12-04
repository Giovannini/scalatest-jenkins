import sbt.Keys._
import sbt._

lazy val root = (project in file(".")).
  settings(
    name := "jenkinspublisher",
    version := "0.0.2-SNAPSHOT",
    organization := "com.github.giovannini",
    scalaVersion := "2.12.4",
    sbtPlugin := true,
    sbtVersion := "1.0.0",
    libraryDependencies ++= Seq(
      "com.lihaoyi"             %% "fastparse"  % "0.4.2",
      "com.softwaremill.sttp"   %% "core"       % "1.1.1",
      "org.scala-lang.modules"  %% "scala-xml"  % "1.0.6"
    ),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation"
    ),
    publishMavenStyle := true,
    publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots"),
    pomIncludeRepository := { _ => false }
  )
