package com.github.giovannini.jenkinspublisher.tasks

import java.io.File
import scala.io.Source
import com.github.giovannini.jenkinspublisher.model.GitHubMessage

object ReadTest {

  def task = {
    val testReportsDirectoryName = "src/project/target/test-reports"
    val testReportsDirectory = new File(testReportsDirectoryName)

    if (testReportsDirectory.isDirectory) {
      GithubPublisher.publishTestResult(
        parseTestFiles(testReportsDirectory)
      )
    } else {
      println("Nothing to print...")
    }
  }

  private def parseTestFiles(testReportsDirectory: File): Seq[GitHubMessage] = {
    val modifiedFiles: Seq[String] = Source.fromFile("modifiedFiles").getLines().toSeq
    val allFiles: Seq[String] = Source.fromFile("allfiles").getLines().toSeq
    sys.env.get("ghprbActualCommit").fold {
      println("Please set env variable 'ghprbActualCommit'.")
      Seq.empty[GitHubMessage]
    } { commitId =>
      for {
        file <- testReportsDirectory.listFiles().toSeq
        if file.isFile
        testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
        failure <- testCase \\ "failure"
        message <- failure.attribute("message").toSeq
        gitHubMessage <- GitHubMessage(message, commitId, testCase, modifiedFiles, allFiles).toSeq
      } yield gitHubMessage
    }
  }
}