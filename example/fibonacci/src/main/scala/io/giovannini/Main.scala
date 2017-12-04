package io.giovannini

object Main extends App {

  val result = EvenFibonnacci.sumFilterFibonacciUnder(4000000, _ % 2 < 1)

  toto()

  println(result)

  @deprecated("This is for testing purpose")
  def toto() = println("deprecated")

}
