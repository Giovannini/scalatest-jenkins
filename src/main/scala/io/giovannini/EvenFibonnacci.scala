package io.giovannini

object EvenFibonnacci {

  def fibonnacci(n: Int): Seq[Int] = {
    @scala.annotation.tailrec
    def fibonnacciRec(n: Int, acc: Seq[Int]): Seq[Int] = {
      if (n <= 0) acc
      else
        fibonnacciRec(n - 1, acc match {
          case Nil            => Seq(1)
          case 1 :: Nil       => Seq(1, 1)
          case x :: y :: tail => (x + y) :: x :: y :: tail
        })
    }

    fibonnacciRec(n, Nil).reverse
  }

  def fibonacciUnder(n: Int): Seq[Int] = {
    @scala.annotation.tailrec
    def fibonnacciUnderRec(n: Int, acc: Seq[Int]): Seq[Int] = {
      if (n <= 0) Nil
      else
        acc match {
          case x :: y :: tail if x < n =>
            fibonnacciUnderRec(n, (x + y) :: x :: y :: tail)
          case other => other
        }
    }

    fibonnacciUnderRec(n, Seq(1, 1)).reverse
  }

  def sumFilterFibonacciUnder(n: Int, condition: Int => Boolean): Int = {
    fibonacciUnder(n).filter(condition).sum
  }

}
