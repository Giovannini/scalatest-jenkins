package com.github.giovannini.jenkinspublisher.github.models

import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{Json, JsonConfiguration}

case class PullRequestComment(
  url: String,
  id: Int,
  path: String,
  position: Int,
  originalPosition: Int,
  commitId: String,
  body: String,
  user: PullRequestCommentUser
)

case class PullRequestCommentUser(
  login: String,
  id: Int
)


object PullRequestCommentUser {
  implicit val jsonConfig = JsonConfiguration(SnakeCase)
  implicit val jsonFormat = Json.format[PullRequestCommentUser]
}

object PullRequestComment {
  implicit val jsonConfig = JsonConfiguration(SnakeCase)
  implicit val jsonFormat = Json.format[PullRequestComment]
}
