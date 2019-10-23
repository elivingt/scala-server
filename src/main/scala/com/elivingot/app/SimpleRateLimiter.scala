package com.elivingot.app

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.TimeUnit

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}


object RateLimiter {
  // todo move to configuration
  val timeRangeMilliseconds = 5000
  val callsPerRange = 5

  // use cache to delete users after 5 minutes to avoid memory leaks
  val cache: LoadingCache[String, SimpleClientRateLimiter] = CacheBuilder.newBuilder()
    .concurrencyLevel(5)
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build[String, SimpleClientRateLimiter](new CacheLoader[String, SimpleClientRateLimiter] {
    def load(id: String) = {
      this.synchronized {
        SimpleClientRateLimiter(id, System.currentTimeMillis(), new AtomicLong(0L))
      }
    }
  })

  def casServe(id: String): Boolean = {
    cache.get(id).casServe(timeRangeMilliseconds,callsPerRange)
  }
}

case class SimpleClientRateLimiter(id: String, var firstTS: Long, count: AtomicLong) {

  def handleTimeRangeEnd(now: Long, timeRangeMilliseconds : Long): Unit = {
    this.synchronized {
      if (now > firstTS + timeRangeMilliseconds) {
        firstTS = now
        count.set(0)
      }
    }
  }

  def casServe(timeRangeMilliseconds : Long,callsPerRange : Long): Boolean = {
    val now = System.currentTimeMillis()
    if (now > firstTS + timeRangeMilliseconds) handleTimeRangeEnd(now,timeRangeMilliseconds)

    count.getAndIncrement()
    if (count.get() <= callsPerRange) true else false
  }
}