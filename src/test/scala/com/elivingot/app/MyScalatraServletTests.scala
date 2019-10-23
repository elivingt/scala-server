package com.elivingot.app

import java.util.concurrent.atomic.AtomicLong

import org.scalatra.test.scalatest._

class MyScalatraServletTests extends ScalatraFunSuite {

  addServlet(classOf[MyScalatraServlet], "/*")

  test("GET /?clientId=<id> on MyScalatraServlet should return status 200 on served request and 503 otherwise") {
    for (i <- 1 to 5) {
      get("/?clientId=test") {
        status should equal(200)
        body should equal("OK")
      }
    }

    get("/?clientId=test") {
      status should equal(503)
      body should equal("Service Unavailable")
    }

    get("/?clientId=another_test") {
      status should equal(200)
      body should equal("OK")
    }
  }

  test("Test RateLimiter") {
    for(_ <- 1 to 5)
      RateLimiter.casServe("test1") should equal(true)
    RateLimiter.casServe("test1") should equal(false)
  }

  test("Test RateLimiter MultiThread") {
    val clients = Map[String, AtomicLong](
      "test_0" -> new AtomicLong(0),
      "test_1" -> new AtomicLong(0),
      "test_2" -> new AtomicLong(0),
      "test_3" -> new AtomicLong(0))

    for (_ <- 1 to 50) {
      val thread = new Thread {
        override def run {
          for (i <- 1 to 100) {
            val id = "test_" + Math.round(Math.random() * 1000) % 4
            if (RateLimiter.casServe(id) == true) {
              clients.getOrElse(id, new AtomicLong(0)).addAndGet(1)
            }
          }
        }
      }
      thread.start
    }

    for (count <- clients.values) {
      count.get() should equal(5)
    }
  }


  test("SimpleClientRateLimiter check for timeRange finished"){
    val limiter = SimpleClientRateLimiter("aa",System.currentTimeMillis(),new AtomicLong(0))
    for(_ <- 1 to 5)
      limiter.casServe(500,5) should equal(true)

    limiter.casServe(500,5) should equal(false)
    Thread.sleep(500)
    limiter.casServe(500,5) should equal(true)
  }

}
