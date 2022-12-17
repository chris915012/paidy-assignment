package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import com.typesafe.config.ConfigFactory
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.errors._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._

class OneFrameClient[F[_]: Applicative] extends Algebra[F] {
  override def get(pair: Rate.Pair): F[Error Either Rate] = {

    val config = ConfigFactory.load("application.conf").getConfig("app")
    val oneFrameConfig = config.getConfig("one-frame")

    implicit val formats: DefaultFormats.type = DefaultFormats
    val session = requests.Session(
      headers = Map("token" -> oneFrameConfig.getString("token"))
    )

    val uri = oneFrameConfig.getString("address") + s"/rates?pair=${pair.from}${pair.to}"
    val parsedJson = parse(session.get(uri).text())
    val price = (parsedJson \ "price").extract[List[Double]]

    val rate = Rate(pair, Price(price.head), Timestamp.now)
    rate.asRight[Error].pure[F]
  }
}
