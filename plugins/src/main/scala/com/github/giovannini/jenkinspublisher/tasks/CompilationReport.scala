package com.github.giovannini.jenkinspublisher.tasks

import scala.sys.process.Process

import com.github.giovannini.jenkinspublisher.model.{Position, Report}
import fastparse.all._

case class CompilationReport(
  warnings: Seq[Report],
  errors: Seq[Report]
)

object CompilationReport {
  def task() = {
    println("CompilationReport...")
    sys.env.get("ghprbActualCommit").fold {
      println("Please set env variable 'ghprbActualCommit'.")
    } { commitId =>
      val report = fetch()
      GithubPublisher.publishTestResult {
        report.warnings.map(_.toGitHubMessage(commitId)) ++
          report.errors.map(_.toGitHubMessage(commitId))
      }
    }
  }

  def fetch(): CompilationReport = {
    val compilationOutput: Stream[String] = Process("sbt -Dsbt.log.noformat=true compile").lineStream_!

    CompilationReport(
      warnings = parseReports("warn", compilationOutput),
      errors = parseReports("error", compilationOutput)
    )
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
    val (filename, line, column, errorTitle, errorLines) = tree
    Report(filename, Position(Some(line), Some(column)), errorTitle ++ errorLines.mkString("\n", "\n", ""))
  }
}
