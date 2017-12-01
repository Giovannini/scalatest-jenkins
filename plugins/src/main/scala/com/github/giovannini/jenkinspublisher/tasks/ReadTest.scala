package com.github.giovannini.jenkinspublisher.tasks

import com.github.giovannini.jenkinspublisher.model.GitHubMessage
import java.io.File

import com.github.giovannini.jenkinspublisher.utils.GitCommands

object ReadTest {

  def task(): Unit = {
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

  private def modifiedFiles: Seq[String] = {
    val modifiedFilesKey = "modifiedFiles"
    GitCommands.diff(modifiedFilesKey).split("\n")
  }

  private def allFiles: Seq[String] = {
    val allfiles = "allfiles"
    GitCommands.lsFiles(allfiles).split("\n")
  }
}
