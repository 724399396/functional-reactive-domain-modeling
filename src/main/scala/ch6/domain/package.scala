package ch6
package domain

import scalaz._
import Scalaz._

package object service {
  type Valid[A] = NonEmptyList[String] \/ A
}
