package com.github.giovannini.jenkinspublisher.model

import com.github.giovannini.jenkinspublisher.tasks.GithubPublisher

import scala.xml.Node

sealed trait GitHubMessage {
  def message: String

  def body: String
}

case class PullRequestGlobalMessage(
  filename: String,
  message: String,
  line: Option[Int]
) extends GitHubMessage {
  val body = s"""
    |{
    |"body": "🐙 Unmodified files make compilation failed : <br>$filename: ${line.map(a => s"$a : ")}$message"
    |}
  """.stripMargin
}

case class PullRequestFileMessage(
  message: String,
  commitId: String,
  path: String,
  line: Option[Int],
  position: Int
) extends GitHubMessage {

  val body = s"""
    |{
    |"body": "$message",
    |"commit_id": "$commitId",
    |"path": "$path",
    |"position": $position
    |}
  """.stripMargin
}

object GitHubMessage {
  def apply(
    message: Seq[Node],
    commitId: String,
    testCase: Node,
    modifiedFiles: Seq[String],
    allfiles: Seq[String],
    contentError: Node
  ): Option[GitHubMessage] = {
    val classname = testCase.attribute("classname").get.text
    buildFilename(classname, modifiedFiles) match {
      case Some(path) =>
        val line = extractLine(contentError, classname.split("\\.").last)
        Some(PullRequestFileMessage(
          message = s"🙅 ${message.text}",
          commitId = commitId,
          path = path,
          line = line,
          position = extractPosition(path, line) + 2 // We made position +1 because zipWithIndex start with 0
        ))
      case None =>
        buildFilename(classname, allfiles).map { filename =>
          PullRequestGlobalMessage(filename, message.text, None)
        }
    }

  }

  private def buildFilename(
    classname: String,
    modifiedFiles: Seq[String]
  ): Option[String] = {
    val fakeFilename = classname.replace(".", "/") + ".scala"
    modifiedFiles.find(_.endsWith(fakeFilename))
  }

  private def extractLine(failure: Node, fileName: String): Option[Int] = {
      val num = s".*$fileName\\.scala:(\\d+).*".r
      num.findFirstIn(failure.text) match {
        case Some(num(line)) => Some(line.toInt)
        case _ => None
      }
  }

  val findFile = "(.*)\\.scala".r
  private def extractPosition(path: String, lineNumber: Option[Int]) : Int = {
    val file = GithubPublisher.findDiff

    val distinctFiles = file.split("diff --git a/").toSeq.flatMap { a =>
      findFile.findFirstIn(a).map(b => (b.split(" ").toSeq.head, a))
    }

    val res: Seq[(Int, Int)] = distinctFiles.
      find(p => p._1.contains(path)).//find diff for contentPath
      map(_._2.split("@@").zipWithIndex.filter(_._2 % 2 ==1).//find linesNumbers
      flatMap{b =>
        val number = b._1.split("\\+")(1).split(",")
        val start = number(0).toInt
        val end = number(0).toInt + number(1).trim.toInt
        (start to end).toList //return a range for lineNumber and zip this to find position
      }.zipWithIndex.toSeq).getOrElse(Seq.empty)

    lineNumber.flatMap( l => res.find(a => a._1 == l).map(_._2)).getOrElse(0)
  }
}
