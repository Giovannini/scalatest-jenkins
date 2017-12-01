package com.github.giovannini.jenkinspublisher.github.models

import java.util.regex.Pattern

import scala.io.Source

case class PullRequestDiffFile(
  filepath: String,
  firstLine: Int
) {
  def position(lineNumber: Int) = lineNumber - firstLine
}

object PullRequestDiff {

  private val LineNumberPattern = """^@@ -([0-9]{1,})(.*)$""".r
  private val FilePattern = """^a\/([^ ]+)(.*)$""".r

  def apply(diff: String): Seq[PullRequestDiffFile] = {
    diff.split(Pattern.quote("diff --git ")).toSeq.tail.flatMap { fileDiff =>
      val lines = Source.fromString(fileDiff).getLines().toList
      val statLine = lines.find(_.contains("@@ ")).getOrElse("")

      (lines.head, statLine) match {
        case (FilePattern(filepath,_), LineNumberPattern(line,_)) =>
          Some(PullRequestDiffFile(filepath, line.toInt))
        case _ => None
      }
    }
  }

}