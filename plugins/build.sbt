import sbt.Keys._
import sbt._

lazy val root = (project in file(".")).
  settings(
    name := "jenkins-publisher",
    version := "0.0.1-SNAPSHOT",
    organization := "io.giovannini",
    scalaVersion := "2.12.4",
    sbtPlugin := true,
    sbtVersion := "1.0.0",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
      "com.softwaremill.sttp" %% "core" % "1.1.1"
    )
  )