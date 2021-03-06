package com.github.giovannini.jenkinspublisher

import com.github.giovannini.jenkinspublisher.tasks.{CompilationReport, ReadTest}
import sbt.{AutoPlugin, Def, settingKey, taskKey}

object Main extends AutoPlugin {

  object autoImport {
    val greeting = settingKey[String]("greeting")

    val hello = taskKey[Unit]("say hello")

    val test2jenkins = taskKey[Unit]("Read test reports")

    val compilationReport = taskKey[Unit]("Send compilation warnings and errors to github pull request")
  }

  import autoImport._

  override def trigger = allRequirements

  override lazy val buildSettings = Seq(
    greeting := "Hi!",
    hello := helloTask.value,
    test2jenkins := readTestTask.value,
    compilationReport := compilationReportTask.value
  )

  lazy val helloTask =
    Def.task {
      println(greeting.value)
    }

  lazy val readTestTask =
    Def.task {
      ReadTest.task()
    }

  lazy val compilationReportTask =
    Def.task {
      CompilationReport.task()
    }
}
