package io.giovannini

import java.io.{File, FileWriter}

import com.softwaremill.sttp._
import io.giovannini.model.PullRequestMessage

import scala.io.Source

object Main {
  implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
  val modifiedFiles: Seq[String] = getModifiedFiles("modifiedFiles")
  val allfiles: Seq[String] = getModifiedFiles("allfiles")

  def main(args: Array[String]): Unit = {
    args.foreach(println)
    val testReportsDirectoryName = args(0)
    val ghprbPullLink = args(1)
    val ghprbIssueLink: String = args(3)
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
      } yield PullRequestMessage(testCase, message, modifiedFiles, allfiles, failure)

      pullRequestMessages
        .foreach(p => p.send(ghprbActualCommit, ghprbPullLink, ghprbIssueLink))

      writeToFile(s"""{"messages": [${pullRequestMessages.map(PullRequestMessage.writes).mkString(", ")}]}""")
    }
  }

  private def writeToFile(s: String): Unit = {
    val fw = new FileWriter("testMessages.json", false)
    try {
      fw.write(s)
    }
    finally fw.close()
  }

  private def getModifiedFiles(source:String): Seq[String] =
    Source.fromFile(source).getLines().toSeq
}