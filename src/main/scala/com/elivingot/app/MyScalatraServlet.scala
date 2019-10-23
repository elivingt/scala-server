package com.elivingot.app

import org.scalatra._

class MyScalatraServlet extends ScalatraServlet  {

  get("/") {
    val id : String= params.getOrElse("clientId","unknown_client")

    if(RateLimiter.casServe(id))
      "OK"
    else {
      status =503
      "Service Unavailable"
    }
  }
}
