package ch4
package patterns
package monoids.foldable

import java.util.Date

import scala.language.higherKinds

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

import ch4.patterns.monoids.foldable.common._

case class Money(m: Map[Currency, Amount]) {
  def toBaseCurrency: Amount = ???
}

case class Transaction(txid: String,
                       accountNo: String,
                       date: Date,
                       amount: Money,
                       txnType: TransactionType,
                       status: Boolean)

case class Balance(b: Money)

trait Analytics[Transaction, Balance, Money] {
  def maxDebitOnDay(txns: List[Transaction])(implicit m: Monoid[Money]): Money
  def sumBalances(bs: List[Balance])(implicit m: Monoid[Money]): Money
}

object Analytics extends Analytics[Transaction, Balance, Money] with Utils {
  import Monoid._

  final val baseCurrency = USD

  private def valueOf(txn: Transaction): Money = {
    if (txn.status) txn.amount
    else
      MoneyAdditionMonoid.op(
        txn.amount,
        Money(Map(baseCurrency -> BigDecimal(100)))
      )
  }

  private def creditBalance(bal: Balance): Money = {
    if (bal.b.toBaseCurrency > 0) bal.b else zeroMoney
  }

  def maxDebitOnDay(
    txns: List[Transaction]
  )(implicit m: Monoid[Money]): Money = {
    mapReduce(txns.filter(_.txnType == DR))(valueOf)
  }

  def sumBalances(bs: List[Balance])(implicit m: Monoid[Money]): Money = {
    mapReduce(bs)(creditBalance)
  }
}
