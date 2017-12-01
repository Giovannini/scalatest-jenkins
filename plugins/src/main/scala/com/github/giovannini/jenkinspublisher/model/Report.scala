package com.github.giovannini.jenkinspublisher.model

case class Report(
  path: String,
  position: Position,
  message: String
)

case class Position(
  line: Option[Int] = None,
  column: Option[Int] = None
)
