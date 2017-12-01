package com.github.giovannini.jenkinspublisher.tasks

import com.softwaremill.sttp._
import com.github.giovannini.jenkinspublisher.model.{GitHubMessage, PullRequestFileMessage, PullRequestGlobalMessage}

object GithubPublisher {

  implicit private val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()

  def publishTestResult(githubMessage: Seq[GitHubMessage]): Unit = {
    (
      sys.env.get("ghprbGhRepository"),
      sys.env.get("ghprbPullId")
    ) match {
      case (None, _) => println("Please set env variable 'ghprbGhRepository'.")
      case (_, None) => println("Please set env variable 'ghprbPullId'.")
      case (Some(ghprbGhRepository), Some(ghprbPullId)) =>
        val ghprbPullLink = s"https://api.github.com/repos/$ghprbGhRepository/pulls/$ghprbPullId/comments"
        val ghprbIssueLink = s"https://api.github.com/repos/$ghprbGhRepository/issues/$ghprbPullId/comments"

        githubMessage
          .foreach(send(ghprbPullLink, ghprbIssueLink))
    }
  }

  private def send(
    ghprbPullLink: String,
    ghprbIssueLink: String
  )(gitHubMessage: GitHubMessage)(implicit v: SttpBackend[Id, Nothing]): Unit = {
    val (body, link) = gitHubMessage match {
      case f: PullRequestFileMessage => (f.body, ghprbPullLink)
      case g: PullRequestGlobalMessage => (g.body, ghprbIssueLink)
    }
    sendMessageOnGitHub(link, body)
  }


  private def sendMessageOnGitHub(
    link: String,
    body: String
  ): Unit = {
    val request = sttp.post(uri"$link")
        .header("Authorization", s"Bearer " +
          "110d1ebcaa" +
          "051686a2c10ecea93" +
          "e57f90a26a5a2")
        .header("Content-Type", "application/json")
      .body(body)

    println(s"Request url: $link")
    println("Request body: " + body)
    val response = request.send()
    println("Response code: " + response.code)
    println("Response body: " + response.body)
  }

  def findDiff : String= {
    (
      sys.env.get("ghprbGhRepository"),
      sys.env.get("ghprbPullId")
    ) match {
      case (None, _) => {
        println("Please set env variable 'ghprbGhRepository'.")
        ""
      }
      case (_, None) => {
        println("Please set env variable 'ghprbPullId'.")
        ""
      }
      case (Some(ghprbGhRepository), Some(ghprbPullId)) =>
        val pr = s"https://api.github.com/repos/${ghprbGhRepository}/pulls/${ghprbPullId}"
        val request = sttp.get(uri"$pr").header("Accept","application/vnd.github.v3.diff")
        val response = request.send()

        response.body.toString

    }
  }
  
}