package io.giovannini.other

import io.giovannini.EvenFibonnacci
import org.scalatest.WordSpec

class EvenFibonnacciTest3 extends WordSpec {

  "fibonnacci" should {

    "return the right value for n = 1" in {
      assert(EvenFibonnacci.fibonnacci(1) == Seq(1))
    }

    "return the right value for n = 2" in {
      assert(EvenFibonnacci.fibonnacci(2) == Seq(1, 1))
    }

    "return the right value for n = 10" in {
      assert(
        EvenFibonnacci.fibonnacci(10) == Seq(1, 1, 2, 3, 5, 8, 13, 21, 34, 55))
    }

  }

}
