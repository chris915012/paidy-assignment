package forex.services.rates.interpreters

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

case class CacheItem[T](value: Double, expirationTime: Long)

class Cache[T] (val maxSize: Int, val expirationTime: FiniteDuration) {
  private val cache = new mutable.HashMap[String, CacheItem[T]]()
  private val evictionQueue = new ListBuffer[String]()

  def get(key: String): Option[Double] = {
    cache.get(key) match {
      case Some(item) if item.expirationTime > System.currentTimeMillis() => Some(item.value)
      case Some(_) => remove(key)
      case None => None
    }
  }

  def put(key: String, value: Double): Unit = {
    remove(key)
    evictionQueue.prepend(key)
    cache.put(key, CacheItem(value, System.currentTimeMillis() + expirationTime.toMillis))
    while (evictionQueue.size > maxSize) {
      remove(evictionQueue.last)
    }
  }

  def remove(key: String): Option[Double] = {
    evictionQueue -= key
    cache.remove(key).map(_.value)
  }
}