package ch4
package patterns
package monoids.foldable

trait Monoid[T] {
  def zero: T
  def op(t1: T, t2: T): T
}

object Monoid {
  def apply[T](implicit monoid: Monoid[T]) = monoid

  implicit val IntAdditionMonoid = new Monoid[Int] {
    override def zero: Int = 0
    override def op(t1: Int, t2: Int): Int = t1 + t2
  }

  implicit val BigDecimalAdditionMonoid = new Monoid[BigDecimal] {
    override def zero: BigDecimal = BigDecimal(0)
    override def op(t1: BigDecimal, t2: BigDecimal): BigDecimal = t1 + t2
  }

  implicit def MapMonoid[K, V: Monoid] = new Monoid[Map[K, V]] {
    override def zero: Map[K, V] = Map.empty[K, V]
    override def op(t1: Map[K, V], t2: Map[K, V]): Map[K, V] =
      t2.foldLeft(t1) { (a, e) =>
        val (key, value) = e
        a.get(key)
          .map(v => a + ((key, implicitly[Monoid[V]].op(v, value))))
          .getOrElse(a + ((key, value)))
      }
  }

  final val zeroMoney: Money = Money(Monoid[Map[Currency, BigDecimal]].zero)

  implicit def MoneyAdditionMonoid = new Monoid[Money] {
    val m = implicitly[Monoid[Map[Currency, BigDecimal]]]
    override def zero: Money = zeroMoney
    override def op(t1: Money, t2: Money): Money = Money(m.op(t1.m, t2.m))
  }

  object MoneyOrdering extends Ordering[Money] {
    def compare(a: Money, b: Money) =
      a.toBaseCurrency compare b.toBaseCurrency
  }

  import MoneyOrdering._
  implicit val MoneyCompareMonoid = new Monoid[Money] {
    override def zero: Money = zeroMoney
    override def op(t1: Money, t2: Money): Money = if (gt(t1, t2)) t1 else t2
  }
}
