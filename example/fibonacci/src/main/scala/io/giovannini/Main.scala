package io.giovannini

object Main extends App {

  val result = EvenFibonnacci.sumFilterFibonacciUnder(4000000, _ % 2 < 1)
  println(result)

}
