package com.github.giovannini.jenkinspublisher.tasks

import java.io.File
import scala.io.Source
import com.softwaremill.sttp._
import com.github.giovannini.jenkinspublisher.model.PullRequestMessage

object ReadTest {

  implicit private val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  def task = {
    val testReportsDirectoryName = "src/project/target/test-reports"
    val testReportsDirectory = new File(testReportsDirectoryName)

    if (testReportsDirectory.isDirectory) {
      val pullRequestMessages = parseTestFiles(testReportsDirectory)
      println(pullRequestMessages.mkString(", "))
      publishTestResult(pullRequestMessages)
    } else {
      println("Nothing to print...")
    }
  }

  private def parseTestFiles(testReportsDirectory: File): Seq[PullRequestMessage] = {
    val modifiedFiles: Seq[String] = Source.fromFile("modifiedFiles").getLines().toSeq

    for {
      file <- testReportsDirectory.listFiles().toSeq
      if file.isFile
      testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
      failure <- testCase \\ "failure"
      message <- failure.attribute("message").toSeq
    } yield PullRequestMessage(testCase, message, modifiedFiles)
  }

  private def publishTestResult(pullRequestMessages: Seq[PullRequestMessage]): Unit = {
    (sys.env.get("ghprbGhRepository"), sys.env.get("ghprbPullLink"), sys.env.get("ghprbPullId")) match {
      case (Some(ghprbGhRepository), Some(ghprbPullId), Some(ghprbActualCommit)) =>
        pullRequestMessages
          .foreach(p => sendMessageOnGitHub(ghprbGhRepository, ghprbActualCommit, p))
      case (None, _, _) => println("Please set env variable 'ghprbGhRepository'.")
      case (_, None, _) => println("Please set env variable 'ghprbPullId'.")
      case (_, _, None) => println("Please set env variable 'ghprbActualCommit'.")
    }
    
  }

  private def sendMessageOnGitHub(
    ghprbGhRepository: String,
    ghprbPullId: String,
    ghprbActualCommit: String,
    pullRequestMessage: PullRequestMessage
  ): Unit = {
    val apiLink = s"https://api.github.com/repos/${ghprbGhRepository}/pulls/${ghprbPullId}/comments"
    val body = s"""
                  |{
                  |"body": "${pullRequestMessage.message}",
                  |"commit_id": "$ghprbActualCommit",
                  |"path": "${pullRequestMessage.fileName}",
                  |"position": ${pullRequestMessage.line.getOrElse(1)}
                  |}
        """.stripMargin
    println("################")
    println(s"Request url: $apiLink")
    println("################")
    val request = sttp.post(uri"$apiLink")
        .header("Authorization", s"Bearer " + "3c114163b1" + "659f0d8f607838" +
          "bb193c122a30dc48")
        .header("Content-Type", "application/json")
      .body(body)

    println("Request body: " + body)
    val response = request.send()
    println("Response code: " + response.code)
    println("Response body: " + response.body)
  }
}