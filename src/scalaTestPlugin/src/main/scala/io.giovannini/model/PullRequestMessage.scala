package io.giovannini.model

import scala.xml.Node

case class PullRequestMessage(
  fileName: String,
  className: String,
  testName: String,
  line: Option[Int],
  message: String
)

object PullRequestMessage {
  def apply(
    testCase: Node,
    message: Seq[Node],
    modifiedFiles: Seq[String]
  ): PullRequestMessage = {
    val classname = testCase.attribute("classname")
    val testName = testCase.attribute("name")
    PullRequestMessage(
      fileName = buildFilename(classname.map(_.text).get, modifiedFiles),
      className = classname.map(_.text).get,
      testName = testName.map(_.text).get,
      line = None,
      message = message.text
    )
  }

  def writes(message: PullRequestMessage): String =
    s"""
       |{
       |"filename": "${message.fileName}",
       |"className": "${message.className}",
       |"testName": "${message.testName}",
       |"line": "${message.line.getOrElse(0)}",
       |"message": "${message.message}"
       |}
      """.stripMargin

  private def buildFilename(classname: String, modifiedFiles: Seq[String]): String = {
    val fakeFilename = classname.replace(".", "/") + ".scala"
    modifiedFiles.find(_.endsWith(fakeFilename)).getOrElse("unknown")
  }
}
