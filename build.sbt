name := "football-league-ranking"

version := "1.0"

scalaVersion := "2.12.3"

lazy val V = new {
  val `akka-http` = "10.0.9"
}


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.19",
  "com.typesafe.akka" %% "akka-http" % V.`akka-http`,
  "io.spray" %%  "spray-json" % "1.3.3",
  "joda-time" % "joda-time" % "2.9.9",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "junit" % "junit" % "4.10" % "test"
)