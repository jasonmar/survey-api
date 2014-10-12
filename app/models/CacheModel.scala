package models

import play.api.cache.Cache
import play.api.Play.current

object CacheModel {

  def actionCounter(id: String, action: String): Int = {
    val key = id + "_" + action
    val currentValue = Cache.getAs[Int](key)
    currentValue match {
      case Some(x) =>
        Cache.set(key, x + 1, 60)
        x + 1
      case _ =>
        Cache.set(key, 1, 60)
        1
    }
  }

}
