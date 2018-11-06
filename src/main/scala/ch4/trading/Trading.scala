package ch4
package trading
import scalaz.Scalaz._
import scalaz._

trait Trading[Account, Trade, ClientOrder, Order, Execution, Market] {
  type Error[A] = NonEmptyList[String]\/A
  type TradingOperation[A] = ListT[Error, A]

  def clientOrders: Kleisli[TradingOperation, List[ClientOrder], Order]
  def execute(m: Market, a: Account): Kleisli[TradingOperation, Order, Execution]
  def allocate(as: List[Account]): Kleisli[TradingOperation, Execution, Trade]

  def tradeGeneration(market: Market,
                      broker: Account,
                      clientAccounts: List[Account]) = {
    clientOrders andThen
      execute(market, broker) andThen
      allocate(clientAccounts)

  }
}
