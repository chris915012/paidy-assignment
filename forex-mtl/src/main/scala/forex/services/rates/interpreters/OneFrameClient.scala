package forex.services.rates.interpreters

import java.util.concurrent.TimeUnit

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import com.typesafe.config.ConfigFactory
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.Errors.Error.OneFrameLookupFailed
import forex.services.rates.Errors._
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, JValue}
import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration

class OneFrameClient[F[_]: Applicative] extends Algebra[F] {
  val cache = new Cache[Int](1000, Duration(5, TimeUnit.MINUTES))

  StatusPrinter.print(LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext])

  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    val config = ConfigFactory.load("application.conf").getConfig("app")
    val oneFrameConfig = config.getConfig("one-frame")

    implicit val formats: DefaultFormats.type = DefaultFormats
    val session = requests.Session(
      headers = Map("token" -> oneFrameConfig.getString("token"))
    )

    val exchangeItem = s"${pair.from}${pair.to}"
    val cachedPrice = cache.get(exchangeItem)
    if(cachedPrice.isEmpty) {
      val uri: String = oneFrameConfig.getString("address") + s"/rates?pair=$exchangeItem"
      val eitherJsonString: Error Either String = Either.catchNonFatal(session.get(uri).text())
        .leftMap(e => OneFrameLookupFailed(e.getMessage))
      eitherJsonString.flatMap { jsonString =>
        val eitherParsedJson: Error Either JValue = Either.catchNonFatal(parse(jsonString)).leftMap(e => OneFrameLookupFailed(e.getMessage))
        eitherParsedJson.map { parsedJson =>
          val price = (parsedJson \ "price").extract[List[Double]]
          cache.put(exchangeItem, price.head)
          Rate(pair, Price(price.head), Timestamp.now)
        }
      }.pure[F]
    } else {
        Rate(pair, Price(BigDecimal.valueOf(cachedPrice.head)), Timestamp.now).asRight[Error].pure[F]
    }
  }
}
