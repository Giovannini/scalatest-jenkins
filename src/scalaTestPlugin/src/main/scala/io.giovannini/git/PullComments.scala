package io.giovannini.git

import com.softwaremill.sttp.{Id, SttpBackend, _}

object PullComments {

  def sendMessageOnGitHub(
    ghprbPullLink: String
  )(body : String)(implicit v : SttpBackend[Id, Nothing]): Unit = {
    val request = sttp.post(uri"$ghprbPullLink")
      .header("Authorization", s"Bearer " + "3c114163b1" + "659f0d8f607838" +
        "bb193c122a30dc48")
      .header("Content-Type", "application/json")
      .body(body)

    println("Request body: " + body)
    val response = request.send()
    println("Response code: " + response.code)
    println("Response body: " + response.body)
  }

}
