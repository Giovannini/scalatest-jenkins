package io.giovannini

import java.io.{File, FileWriter}
import scala.io.Source

import com.softwaremill.sttp._
import io.giovannini.model.PullRequestMessage

object Main {
  implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
  val modifiedFiles: Seq[String] = getModifiedFiles

  def main(args: Array[String]): Unit = {
    args.foreach(println)
    val testReportsDirectoryName = args(0)
    val ghprbPullLink = args(1)
    val ghprbActualCommit = args(2)


    sttp.auth.basic("khand19", "3c114163b1" + "659f0d8f607838" +
      "bb193c122a30dc48")

    val testReportsDirectory = new File(testReportsDirectoryName)
    if (testReportsDirectory.isDirectory) {
      val pullRequestMessages = for {
        file <- testReportsDirectory.listFiles().toSeq
        if file.isFile
        testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
        failure <- testCase \\ "failure"
        message <- failure.attribute("message").toSeq
        
      } yield PullRequestMessage(testCase, message, modifiedFiles)

      pullRequestMessages
        .foreach(p => sendMessageOnGitHub(ghprbPullLink, ghprbActualCommit, p))

      writeToFile(s"""{"messages": [${pullRequestMessages.map(PullRequestMessage.writes).mkString(", ")}]}""")
    }
  }

  def sendMessageOnGitHub(
    ghprbPullLink: String,
    ghprbActualCommit: String,
    pullRequestMessage: PullRequestMessage
  ): Unit = {
    val body = s"""
                  |{
                  |"body": "${pullRequestMessage.message}",
                  |"commit_id": "$ghprbActualCommit",
                  |"path": "${pullRequestMessage.fileName}",
                  |"position": ${pullRequestMessage.line.getOrElse(1)}
                  |}
        """.stripMargin
    val request = sttp.post(uri"$ghprbPullLink")
        .header("Authorization", s"Bearer " + "3c114163b1" + "659f0d8f607838" +
          "bb193c122a30dc48")
        .header("Content-Type", "application/json")
      .body(body)

    println("Request body: " + body)
    val response = request.send()
    println("Response code: " + response.code)
    println("Response body: " + response.body)
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
}