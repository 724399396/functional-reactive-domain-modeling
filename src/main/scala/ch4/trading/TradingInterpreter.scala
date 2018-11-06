package ch4
package trading

import ch4.trading.TradeModel._
import scalaz.Kleisli._
import scalaz.{ Order => _, _}
import Scalaz._

trait TradingInterpreter
    extends Trading[Account, Trade, ClientOrder, Order, Execution, Market] {
  def clientOrders: Kleisli[TradingOperation, List[ClientOrder], Order] =
    kleisli[TradingOperation, List[ClientOrder], Order](orders => ListT(fromClientOrders(orders).traverse[Error,Order](_.right)))

  def execute(market: Market, brokerAccount: Account) =
    kleisli[TradingOperation, Order, Execution] { order =>
      ListT(order.items.traverse[Error, Execution] { item =>
        Execution(
          brokerAccount,
          item.ins,
          "e-123",
          market,
          item.price,
          item.qty
        ).right
      })
    }

  def allocate(accounts: List[Account]) = kleisli[TradingOperation, Execution, Trade] {
    execution =>
      val q = execution.quantity / accounts.size
      ListT(accounts.traverse[Error,Trade] { account =>
        makeTrade(
          account,
          execution.instrument,
          "t-123",
          execution.market,
          execution.unitPrice,
          q
        ).right
      })
  }
}

object TradingInterpreter extends TradingInterpreter
