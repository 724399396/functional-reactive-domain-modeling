package ch3.algebra.interpreter

import java.util.Date

import ch3.algebra.AccountService
import ch3.algebra.interpreter.common._

import scala.util.{Failure, Success, Try}

object AccountService extends AccountService[Account, Amount, Balance] {
  override def open(no: String,
                    name: String,
                    openingDate: Option[Date]): Try[Account] = {
    if (no.isEmpty || name.isEmpty)
      Failure(new Exception(s"Account no or name annot be blank"))
    else if (openingDate.getOrElse(today) before today)
      Failure(new Exception(s"Cannot open account in the past"))
    else Success(Account(no, name, openingDate.getOrElse(today)))
  }

  override def close(account: Account,
                     closeDate: Option[Date]): Try[Account] = {
    val cd = closeDate.getOrElse(today)
    if (cd before account.dateOfOpen) {
      Failure(
        new Exception(
          s"Close date $cd cannot be before opening date ${account.dateOfOpen}"
        )
      )
    } else Success(account.copy(dateOfClose = Some(cd)))
  }

  override def debit(account: Account, amount: Amount): Try[Account] = {
    if (account.balance.amount < amount)
      Failure(new Exception("Insufficient balance"))
    else
      Success(account.copy(balance = Balance(account.balance.amount - amount)))
  }

  override def credit(account: Account, amount: Amount): Try[Account] = {
    Success(account.copy(balance = Balance(account.balance.amount + amount)))
  }

  override def balance(account: Account): Try[Balance] =
    Success(account.balance)
}
