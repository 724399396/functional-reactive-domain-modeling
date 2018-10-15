package ch3
package repository
import java.util.{Calendar, Date}

object common {
  type Amount = BigDecimal

  val today = Calendar.getInstance.getTime
}

import ch3.repository.common._

case class Balance(amount: Amount = 0)

case class Account(no: String,
                   name: String,
                   dateOfOpening: Date = today,
                   dateOfClosing: Option[Date] = None,
                   balance: Balance = Balance(0)) {}
