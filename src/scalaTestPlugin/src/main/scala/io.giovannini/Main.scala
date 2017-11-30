package io.giovannini

import java.io.{File, FileWriter}
import scala.io.Source
import scala.xml.Node

object Main {
  def main(args: Array[String]): Unit = {
    val testReportsDirectoryName = args.head
    val modifiedFiles = getModifiedFiles

    val testReportsDirectory = new File(testReportsDirectoryName)
    if (testReportsDirectory.isDirectory) {
      val jsValues = (for {
        file <- testReportsDirectory.listFiles().toSeq
        if file.isFile
        testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
        failure <- testCase \\ "failure"
        message <- failure.attribute("message").toSeq
      } yield PullRequestMessage(testCase, message, modifiedFiles))
        .map(PullRequestMessage.writes)

      writeToFile(s"""{"messages": [${jsValues.mkString(", ")}]}""")
    }
  }

  private def writeToFile(s: String): Unit = {
    val fw = new FileWriter("testMessages.json", false)
    try {
      fw.write(s)
    }
    finally fw.close()
  }

  private def getModifiedFiles: Seq[String] =
    Source.fromFile("modifiedFiles").getLines().toSeq

  private def buildFilename(classname: String, modifiedFiles: Seq[String]): String = {
    val fakeFilename = classname.replace(".", "/") + ".scala"
    println(modifiedFiles.mkString(","))
    println(fakeFilename)
    modifiedFiles.find(_.endsWith(fakeFilename)).getOrElse("unknown")
  }

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
  }
}