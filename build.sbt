name := "functional_reactive_domain_modeling"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.26",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.26",
  "joda-time" % "joda-time" % "2.9.1",
  "org.joda" % "joda-convert" % "1.8.1",
  "io.spray" %% "spray-json" % "1.3.2",
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.17",
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.h2database" % "h2" % "1.4.197",
  "com.zaxxer" % "HikariCP-java6" % "2.3.8",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
)

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-language:postfixOps",
  "-deprecation"
)
