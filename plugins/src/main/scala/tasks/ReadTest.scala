package io.giovannini.jenkinspublisher.tasks

import java.io.File
import scala.io.Source
import io.giovannini.jenkinspublisher.model.PullRequestMessage

object ReadTest {

  def task = {
    val modifiedFiles: Seq[String] = Source.fromFile("modifiedFiles").getLines().toSeq
    val testReportsDirectoryName = "src/project/target/test-reports"
    val ghprbPullLink = sys.env.get("ghprbPullLink")
    val ghprbActualCommit = sys.env.get("ghprbActualCommit")
    val testReportsDirectory = new File(testReportsDirectoryName)

    if (testReportsDirectory.isDirectory) {
      println(parseTestFiles(testReportsDirectory, modifiedFiles).mkString(", "))
    } else {
      println("Nothing to print...")
    }
  }

  private def parseTestFiles(
    testReportsDirectory: File,
    modifiedFiles: Seq[String]
  ): Seq[PullRequestMessage] = {
    for {
      file <- testReportsDirectory.listFiles().toSeq
      if file.isFile
      testCase <- scala.xml.XML.loadFile(file) \\ "testsuite" \\ "testcase"
      failure <- testCase \\ "failure"
      message <- failure.attribute("message").toSeq
    } yield PullRequestMessage(testCase, message, modifiedFiles)
  }
}