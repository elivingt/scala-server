val ScalatraVersion = "2.7.0-RC1"

organization := "com.elivingot"

name := "My First Scalatara Web App"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.6"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
)
libraryDependencies += "com.github.cb372" %% "scalacache-guava" % "0.9.4"


enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
