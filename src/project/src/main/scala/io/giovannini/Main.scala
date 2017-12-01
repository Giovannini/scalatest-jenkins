package io.giovannini

object Main extends App {

  val result = EvenFibonnacci.sumFilterFibonacciUnder(4000000, _ % 2 < 1)

  val x = Test.test()

  println(result)

}

object Test {
  @deprecated("Do not use this method", "Lib 3.0")
  def test() = ()
}
