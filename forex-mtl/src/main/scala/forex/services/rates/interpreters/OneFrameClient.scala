package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.errors._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

class OneFrameClient[F[_]: Applicative] extends Algebra[F] {
  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    implicit val formats = DefaultFormats
    val session = requests.Session(
      headers = Map("token" -> "10dc303535874aeccc86a8251e6992f5")
    )
    val response = session.get("http://localhost:8080/rates?pair=USDJPY")
    val responseText = response.text()
    val parsedJson = parse(responseText)
    val value1 = (parsedJson \ "price").extract[List[Double]]

    val rate = Rate(pair, Price(value1.head), Timestamp.now)
    rate.asRight[Error].pure[F]
  }
}
