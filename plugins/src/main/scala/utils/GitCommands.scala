package utils

import scala.sys.process._

object GitCommands {

  def diff(outputFilename: String): String = {
    "git diff --name-only origin/master".!!
  }

  def lsFiles(outputFilename: String): String = {
    "git ls-files".!!
  }
}
