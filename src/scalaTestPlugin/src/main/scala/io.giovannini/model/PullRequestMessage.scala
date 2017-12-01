package io.giovannini.model

import com.softwaremill.sttp.{Id, SttpBackend}
import io.giovannini.git.PullComments

import scala.xml.Node

case class PullRequestMessage(
  fileName: Either[Option[String], String],
  className: String,
  testName: String,
  line: Option[Int],
  message: String
) {
  def send(
    actualCommit: String,
    ghprbPullLink: String,
    ghprbIssueLink: String
  )(implicit v: SttpBackend[Id, Nothing]) = {
    val (body, link) = fileName match {
      case Right(file) => (
        s"""
           |{
           |"body": "$message",
           |"commit_id": "$actualCommit",
           |"path": "$file",
           |"position": ${position}
           |}
        """.stripMargin, ghprbPullLink)
      case Left(b) => (
        s"""
           |{
           |"body": "Unmodified files make compilation failed :<br> ${b.getOrElse("")} : ${line.map(a => s"$a : ")
        }$message"
           |}
        """.stripMargin, ghprbIssueLink)
    }
    PullComments.sendMessageOnGitHub(link)(body)
  }

  def position: Int = 1
}

object PullRequestMessage {
  def apply(
    testCase: Node,
    message: Seq[Node],
    modifiedFiles: Seq[String],
    allfiles: Seq[String],
    contentError: Node
  ): PullRequestMessage = {
    val classname = testCase.attribute("classname")
    val testName = testCase.attribute("name")
    PullRequestMessage(
      fileName = buildFilename(classname.map(_.text).get, modifiedFiles, allfiles),
      className = classname.map(_.text).get,
      testName = testName.map(_.text).get,
      line = extractLine(contentError, classname.map(_.text).get.split("\\.").last),
      message = message.text
    )
  }

  def writes(message: PullRequestMessage): String = {
    s"""
{
"filename": "${message.fileName}",
"className": "${message.className}",
"testName": "${message.testName}",
"line": "${message.line.getOrElse(0)}",
"message": "${message.message}"
}
      """.stripMargin
  }

  private def buildFilename(classname: String, modifiedFiles: Seq[String], allfiles: Seq[String]):
  Either[Option[String], String] = {
    val fakeFilename = classname.replace(".", "/") + ".scala"
    modifiedFiles.find(_.endsWith(fakeFilename)) match {
      case None => Left(allfiles.find(_.endsWith(fakeFilename)))
      case Some(fileName) => Right(fileName)
    }
  }

  def extractLine(failure: Node, fileName: String): Option[Int] = {
    val num = s".*$fileName\\.scala:(\\d+).*".r
    num.findFirstIn(failure.text) match {
      case Some(num(line)) => Some(line.toInt)
      case _ => None
    }
  }
}
