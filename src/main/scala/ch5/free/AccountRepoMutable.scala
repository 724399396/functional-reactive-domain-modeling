package ch5
package free

import scala.collection.mutable.{Map => MMap}
import scalaz._
import Scalaz._
import scalaz.concurrent.Task
import Task.{now, fail}

trait AccountRepoInterpreter {
  def apply[A](account: AccountRepo[A]): Task[A]
}

case class AccountRepoMutableInterpreter() extends AccountRepoInterpreter {
  val table: MMap[String, Account] = MMap.empty[String, Account]

  val step: AccountRepoF ~> Task = new (AccountRepoF ~> Task) {
    override def apply[A](fa: AccountRepoF[A]): Task[A] = fa match {
      case Query(no) =>
        table.get(no).map {a => now(a)}
          .getOrElse{ fail(new RuntimeException(s"Account no $no not found")) }
      case Store(account) => now (table += ((account.no, account))).void
      case Delete(no) => now(table -= no).void
    }
  }

  /**
    * Turns the AccountRepo script into a `Task` that executes it in a mutable setting
    */
  def apply[A](action: AccountRepo[A]): Task[A] = action.foldMap(step)
}
