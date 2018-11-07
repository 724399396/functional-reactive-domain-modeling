package ch6
package domain
package repository

import java.util.Date

import domain.model.{Account, Balance}
import scalaz.Scalaz._
import scalaz._

trait AccountRepository {
  def query(no: String): \/[NonEmptyList[String], Option[Account]]
  def store(a: Account): \/[NonEmptyList[String], Account]
  def balance(no: String): \/[NonEmptyList[String], Balance] = query(no) match {
    case \/-(Some(a)) => a.balance.right
    case \/-(None) => NonEmptyList(s"No account exist with no $no").left[Balance]
    case a @ -\/(_) => a
  }
  def query(openedOn: Date): \/[NonEmptyList[String], Seq[Account]]
  def all: \/[NonEmptyList[String], Seq[Account]]
}
