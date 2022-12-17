package forex.domain

import cats.Show
import cats.syntax.show._
import io.circe.Json
import io.circe.Encoder

sealed trait Currency extends Product with Serializable {
  def code: String
}

object Currency {
  case object AUD extends Currency { val code: String = "AUD" }
  case object CAD extends Currency { val code: String = "CAD" }
  case object CHF extends Currency { val code: String = "CHF" }
  case object EUR extends Currency { val code: String = "EUR" }
  case object GBP extends Currency { val code: String = "GBP" }
  case object NZD extends Currency { val code: String = "NZD" }
  case object JPY extends Currency { val code: String = "JPY" }
  case object SGD extends Currency { val code: String = "SGD" }
  case object USD extends Currency { val code: String = "USD" }

  val all: Set[Currency] = Set(AUD, CAD, CHF, EUR, GBP, NZD, JPY, SGD, USD)

  implicit val show: Show[Currency] = _.code.show
  implicit val encoder: Encoder[Currency] =
    Encoder.instance[Currency] { c => Json.fromString(c.code) }

  def fromString(s: String): Currency = all.find(_.code == s.toUpperCase).get

}
