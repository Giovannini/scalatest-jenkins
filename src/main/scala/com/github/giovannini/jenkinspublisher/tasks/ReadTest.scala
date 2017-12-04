package com.github.giovannini.jenkinspublisher.tasks

import com.github.giovannini.jenkinspublisher.model.GitHubMessage
import java.io.File

import com.github.giovannini.jenkinspublisher.utils.GitCommands

object ReadTest {

  def task(): Unit = {
    val testReportsDirectoryName = "target/test-reports"
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
    sys.env.get("ghprbActualCommit").fold {
      println("Please set env variable 'ghprbActualCommit'.")
      Seq.empty[GitHubMessage]
    } { commitId =>
      println(s"Parsing test files for commit $commitId...")
      val result = for {
        file <- testReportsDirectory.listFiles().toSeq
        if file.isFile
        testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
        failure <- testCase \\ "failure"
        message <- failure.attribute("message").toSeq
        gitHubMessage <- GitHubMessage(message, commitId, testCase, modifiedFiles, allFiles, failure).toSeq
      } yield gitHubMessage
      println(s"Test files parsed. ${result.length} message${if (result.length > 1) "s" else ""} to send.")
      result
    }
  }

  private def modifiedFiles: Seq[String] = {
    val modifiedFilesKey = "modifiedFiles"
    GitCommands.diff(modifiedFilesKey).split("\n")
  }

  private def allFiles: Seq[String] = {
    val allfiles = "allfiles"
    GitCommands.lsFiles(allfiles).split("\n")
  }
}
