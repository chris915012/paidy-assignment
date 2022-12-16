package forex.services.rates

import cats.Applicative
import interpreters._

object Interpreters {
  def live[F[_]: Applicative]: Algebra[F] = new OneFrameClient[F]()
}
