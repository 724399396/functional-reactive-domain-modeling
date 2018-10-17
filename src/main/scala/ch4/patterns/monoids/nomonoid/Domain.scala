package ch4
package patterns
package monoids.nomonoid

sealed trait TransactionType
case object DR extends TransactionType
case object CR extends TransactionType

sealed trait Currency
case object USD extends Currency
case object JPY extends Currency
case object AUD extends Currency
case object INR extends Currency

object common {
  type Amount = BigDecimal
}

import java.util.Date

case class Money(m: Map[Currency, BigDecimal]) {
  def +(that: Money) = {
    val n = that.m.foldLeft(m) { (a, e) =>
      val (cyy, amt) = e
      a.get(cyy)
        .map { amount => a + ((cyy, amt + amount))
        }
        .getOrElse(a + ((cyy, amt)))
    }
    Money(n)
  }

  def toBaseCurrency: BigDecimal = ???
}

object Money {
  val zeroMoney = Money(Map.empty[Currency, BigDecimal])
}

import ch4.patterns.monoids.nomonoid.Money._
object MoneyOrdering extends Ordering[Money] {
  def compare(a: Money, b: Money) = a.toBaseCurrency compare b.toBaseCurrency
}

import ch4.patterns.monoids.nomonoid.MoneyOrdering._

case class Transaction(txid: String,
                       accountNo: String,
                       date: Date,
                       amount: Money,
                       txnType: TransactionType,
                       status: Boolean)

case class Balance(b: Money)

trait Analytics[Transaction, Balance, Money] {
  def maxDebitOnDay(txns: List[Transaction]): Money
  def sumBalances(bs: List[Balance]): Money
}

object Analytics extends Analytics[Transaction, Balance, Money] {
  override def maxDebitOnDay(txns: List[Transaction]): Money = {
    txns.filter(_.txnType == DR).foldLeft(zeroMoney) { (a, txn) =>
      if (gt(txn.amount, a)) txn.amount else a
    }
  }

  override def sumBalances(bs: List[Balance]): Money =
    bs.foldLeft(zeroMoney)(_ + _.b)
}
