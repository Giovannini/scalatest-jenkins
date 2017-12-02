package com.github.giovannini.jenkinspublisher.tasks

import scala.sys.process.Process

import fastparse.all._

import com.github.giovannini.jenkinspublisher.model.GitHubMessage
import com.github.giovannini.jenkinspublisher.model.{Position, Report}
import com.github.giovannini.jenkinspublisher.utils.GitCommands

case class CompilationReport(
  warnings: Seq[Report],
  errors: Seq[Report]
)

object CompilationReport {
  def task(): Unit = {
    val compilationOutput: Seq[String] = Process(
      "sbt -Dsbt.log.noformat=true clean test:compile"
    ).lineStream_!

    compilationOutput.foreach(println)

    val warnings = parseReports("warn", compilationOutput)
    val errors = parseReports("error", compilationOutput)

    val githubMessages: Seq[GitHubMessage] =
      sys.env.get("ghprbActualCommit") match {
        case Some(commitId) =>
          Seq(
            ("warning", warnings),
            ("error", errors)
          ).flatMap { case (message, reports) =>
              reports.flatMap(GitHubMessage(message, commitId, _, modifiedFiles, allFiles))
            }
        case _ =>
          println("Please set env variable 'ghprbActualCommit'.")
          Seq.empty[GitHubMessage]
      }

    if (githubMessages.nonEmpty) {
      GithubPublisher.publishTestResult(githubMessages)
      sys.exit(1)
    }
  }

  private def modifiedFiles: Seq[String] = {
    val modifiedFilesKey = "modifiedFiles"
    GitCommands.diff(modifiedFilesKey).split("\n")
  }

  private def allFiles: Seq[String] = {
    val allfiles = "allfiles"
    GitCommands.lsFiles(allfiles).split("\n")
  }

  private def parseReports(kind: String, lines: Seq[String]): Seq[Report] = {
    def reports: P[Seq[Report]] = P( report.rep )
    def report: P[Report] = P( reportFirstLine ~ reportLines.rep(1) ).map(reportFromTree)
    def reportFirstLine = P( CharsWhile(_ != ':').! ~ ":" ~ number ~ ":" ~ number ~ ": " ~ CharsWhile(_ != '\n').! ~ "\n" )
    def reportLines: P[String] = P( &(CharsWhile(_ != '/')) ~ CharsWhile(_ != '\n').! ~ "\n" )
    def number: P[Int] = P( CharIn('0' to '9').rep(1).!.map(_.toInt) )

    val input = lines
      .filter(_.startsWith(s"[$kind]"))
      .map(_.split(" ").drop(1).mkString(" "))
      .dropRight(2)
      .mkString("\n")

    reports.parse(input) match {
      case Parsed.Success(reports, _) => reports
      case Parsed.Failure(expected, failedIndex, extra) =>
        println(s"Failure: expected $expected at $failedIndex. $extra")
        Nil
    }
  }

  private def reportFromTree(tree: (String, Int, Int, String, Seq[String])): Report = {
    val (filename, line, column, errorTitle, _) = tree
    Report(filename, Position(Some(line), Some(column)), errorTitle)
  }
}
