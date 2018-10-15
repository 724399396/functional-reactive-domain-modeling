package ch3
package repository
package partial

import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](a: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[_]: Functor]: Functor[F] =
    implicitly[Functor[F]]

  implicit def ListFunctor: Functor[List] = new Functor[List] {
    override def map[A, B](a: List[A])(f: A => B): List[B] = a map f
  }

  implicit def OptionFunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](a: Option[A])(f: A => B): Option[B] = a map f
  }

  implicit def Tuple2Functor[A1]: Functor[({ type f[x] = (A1, x) })#f] =
    new Functor[({ type f[x] = (A1, x) })#f] {
      override def map[A, B](a: (A1, A))(f: A => B): (A1, B) = (a._1, f(a._2))
    }

  implicit def Function1Functor[A1]: Functor[({ type f[x] = (A1) => x })#f] =
    new Functor[({ type f[x] = (A1) => x })#f] {
      override def map[A, B](fa: A1 => A)(f: A => B) = fa andThen f
    }
}

object FunctorTest {
  import Functor._

  val x = List(1, 2, 3, 4)
  val f: Int => Int = _ + 1

  Functor[List].map(x)(f)

  val l = List(("a", 10), ("b", 20))
  Functor[List].map(l)(t => Functor[({ type f[x] = (String, x) })#f].map(t)(f))

  List(1, 2, 3) map f
  l.map(e => Functor[({ type f[x] = (String, x) })#f].map(e)(f))

  type Tup[A] = (String, A)
  l.map(e => Functor[Tup].map(e)(f))
}
