package io.giovannini

import java.io.{File, FileWriter}

object Main {
  def main(args: Array[String]): Unit = {
    val testReportsDirectoryName = args.head

    val testReportsDirectory = new File(testReportsDirectoryName)
    if (testReportsDirectory.isDirectory) {
      val jsValues = (for {
        file <- testReportsDirectory.listFiles().toSeq
        if file.isFile
        testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
        failure <- testCase \\ "failure"
        message <- failure.attribute("message").toSeq
      } yield {
        val classname = testCase.attribute("classname")
        val testName = testCase.attribute("name")
        PullRequestMessage(
          fileName = classname.map(_.text).get.replace(".", "/") + ".scala",
          className = classname.map(_.text).get,
          testName = testName.map(_.text).get,
          line = None,
          message = message.text
        )
      }).map(PullRequestMessage.writes)

      writeToFile(s"""{"messages": [${jsValues.mkString(", ")}]}""")
    }
  }

  def writeToFile(s: String): Unit = {
    val fw = new FileWriter("testMessages.json", false)
    try {
      fw.write(s)
    }
    finally fw.close()
  }

  case class PullRequestMessage(
    fileName: String,
    className: String,
    testName: String,
    line: Option[Int],
    message: String
  )

  object PullRequestMessage {
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