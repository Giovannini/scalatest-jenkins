package com.github.giovannini.jenkinspublisher.model

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
    |"body": "Unmodified files make compilation failed : $filename : ${line.map(a => s"$a : ")}$message"
    |}
  """.stripMargin
}

case class PullRequestFileMessage(
  message: String,
  commitId: String,
  path: String,
  position: Option[Int]
) extends GitHubMessage {

  val body = s"""
    |{
    |"body": "$message",
    |"commit_id": "$commitId",
    |"path": "$path",
    |"position": ${position.getOrElse(1)}
    |}
  """.stripMargin
}

object GitHubMessage {
  def apply(
    message: Seq[Node],
    commitId: String,
    testCase: Node,
    modifiedFiles: Seq[String],
    allfiles: Seq[String]
  ): Option[GitHubMessage] = {
    val classname = testCase.attribute("classname").get.text
    buildFilename(classname, modifiedFiles) match {
      case Some(path) =>
        Some(PullRequestFileMessage(
          message = message.text,
          commitId = commitId,
          path = path,
          position = None
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
}
