package com.github.giovannini.jenkinspublisher.github

import play.api.libs.json.Json

import com.github.giovannini.jenkinspublisher.github.models.PullRequestComment
import com.softwaremill.sttp._

object GithubApi {

  private val baseUrl = "https://api.github.com"

  def loadPullRequestDiff(repo: String, number: Int)(implicit backend: SttpBackend[Id, Nothing]) = {
    val apiUrl = s"$baseUrl/repos/$repo/pulls/$number"

    val request = sttp.get(uri"$apiUrl")
      .header("Accept", "application/vnd.github.v3.diff")

    request.send().body
  }

  def loadPullRequestComments(repo: String, number: Int)(implicit backend: SttpBackend[Id, Nothing]): Either[String, Seq[PullRequestComment]] = {
    val apiUrl = s"$baseUrl/repos/$repo/pulls/$number/comments"

    val request = sttp
      .get(uri"$apiUrl")

    request.send().body.map(body =>
      Json.parse(body).as[Seq[PullRequestComment]]
    )
  }

}
