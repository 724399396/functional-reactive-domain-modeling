package ch3.algebra.interpreter

import java.util.{Calendar, Date}

object common {
  type Amount = BigDecimal
  def today = Calendar.getInstance().getTime
}

import common._

case class Balance(amount: Amount = 0)

case class Account(no: String, name: String, dateOfOpen: Date = today, dateOfClose: Option[Date] = None,
                   balance: Balance = Balance(0)) {

}
