package forex.services.rates.interpreters

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.either._
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import com.typesafe.config.ConfigFactory
import forex.domain.{Price, Rate, Timestamp}
import forex.services.rates.Algebra
import forex.services.rates.errors._
import org.json4s.{DefaultFormats, JValue}
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

class OneFrameClient[F[_]: Applicative] extends Algebra[F] {

  def logger: Logger = LoggerFactory.getLogger("OneFrame")
  StatusPrinter.print((LoggerFactory.getILoggerFactory).asInstanceOf[LoggerContext])

  override def get(pair: Rate.Pair): F[Error Either Rate] = {

    val config = ConfigFactory.load("application.conf").getConfig("app")
    val oneFrameConfig = config.getConfig("one-frame")

    implicit val formats: DefaultFormats.type = DefaultFormats
    val session = requests.Session(
      headers = Map("token" -> oneFrameConfig.getString("token"))
    )

    var jsonString: String = ""
    var uri: String = ""

    try {
      uri = oneFrameConfig.getString("address") + s"/rates?pair=${pair.from}${pair.to}"
      jsonString = session.get(uri).text()
    } catch {
      case _: Exception => logger.error("ERROR: " + uri).pure[F]
    }
    val parsedJson: JValue = parse(jsonString)
    val price = (parsedJson \ "price").extract[List[Double]]
    val rate = Rate(pair, Price(price.head), Timestamp.now)
    rate.asRight[Error].pure[F]

  }
}
