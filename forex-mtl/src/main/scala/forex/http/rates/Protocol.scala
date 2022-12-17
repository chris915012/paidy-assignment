package forex.http
package rates

import cats.Show
import forex.domain.Currency.{all, show}
import forex.domain.Rate.Pair
import forex.domain._
import io.circe._
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveConfiguredEncoder

object Protocol {
  implicit val configuration: Configuration = Configuration.default.withSnakeCaseMemberNames

  final case class GetApiRequest(from: Currency, to: Currency)
  final case class GetApiResponse(from: Currency, to: Currency, price: Price, timestamp: Timestamp)

  implicit val currencyShow: Show[Currency] = show
  implicit val currencyEncoder: Encoder[Currency] =
    Encoder.instance[Currency] { show.show _ andThen Json.fromString }
  implicit val currencyDecoder: Decoder[Currency] =
    Decoder.decodeString.emap(s => all.find(_.code == s).toRight(s"Invalid currency: $s"))

  implicit val pairEncoder: Encoder[Pair] = deriveConfiguredEncoder[Pair]
  implicit val rateEncoder: Encoder[Rate] = deriveConfiguredEncoder[Rate]
  implicit val responseEncoder: Encoder[GetApiResponse] = deriveConfiguredEncoder[GetApiResponse]
}
