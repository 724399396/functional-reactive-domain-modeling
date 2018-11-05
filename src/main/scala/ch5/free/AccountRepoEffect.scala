package ch5
package free

import scalaz.Scalaz._
import scalaz._

object AccountRepoState {
  type AccountMap = Map[String, Account]
  type Err[A] = Error \/ A
  type AccountState[A] = StateT[Err, AccountMap, A]

  trait AccountRepoInterpreter[M[_]] {
    def apply[A](aacount: AccountRepo[A]): M[A]
  }

  case class AccountRepoMutableInterpreter() extends AccountRepoInterpreter[AccountState] {
    val step: AccountRepoF ~> AccountState = new (AccountRepoF ~> AccountState) {
      override def apply[A](fa: AccountRepoF[A]): AccountState[A] = fa match {
        case Query(no) => for {
          table <- StateT.get[Err,AccountMap]
          a <- StateT.liftM[Err, AccountMap, Account](table.get(no).map { a => a.right }
            .getOrElse {
              new Error(s"Account no $no not found").left
            })
        } yield a
        case Store(account) => for {
          table <- StateT.get[Err, AccountMap]
          _ <- StateT.put[Err, AccountMap](table + ((account.no, account)))
        } yield ()
        case Delete(no) => for {
          table <- StateT.get[Err, AccountMap]
          _ <- StateT.put[Err, AccountMap](table - no)
        } yield ()
      }
    }

    /**
      * Turns the AccountRepo script into a `Task` that executes it in a mutable setting
      */
    def apply[A](action: AccountRepo[A]): AccountState[A] = action.foldMap(step)
  }

}
