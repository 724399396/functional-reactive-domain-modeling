package ch4
package patterns

import scala.language.higherKinds

trait Applicative[F[_]] extends Functor[F] {
  def unit[A](a: => A): F[A]

  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
    println(s"+++ app $fa $fb")
    ap(map(fa)(f.curried))(fb)
  }

  def ap[A, B](f: F[A => B])(a: F[A]): F[B]

  def sequence[A](fas: List[F[A]]): F[List[A]] =
    traverse(fas)(fa => fa)

  def traverse[A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(unit(List[B]())) { (x, acc) =>
      println(s"from app $x"); map2(f(x), acc)(_ :: _)
    }
}

object Applicative {
  def apply[F[_]: Applicative]: Applicative[F] =
    implicitly[Applicative[F]]

  implicit def ListApply: Applicative[List] = new Applicative[List] {
    override def unit[A](a: => A): List[A] = List(a)
    override def ap[A, B](fs: List[A => B])(as: List[A]): List[B] =
      for {
        a <- as
        f <- fs
      } yield f(a)
    override def map[A, B](a: List[A])(f: A => B): List[B] = a map f
  }
}
