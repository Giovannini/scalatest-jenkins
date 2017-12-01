package com.github.giovannini.jenkinspublisher.model

case class Report(
  path: String,
  position: Position,
  message: String
) {
  def toGitHubMessage(commitId: String) = PullRequestFileMessage(
    message = message,
    commitId = commitId,
    path = path,
    position = position.line
  )
}

case class Position(
  line: Option[Int] = None,
  column: Option[Int] = None
)
