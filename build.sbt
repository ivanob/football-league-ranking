name := "football-league-ranking"

version := "1.0"

scalaVersion := "2.12.3"

lazy val V = new {
  val `akka-http` = "10.0.9"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.19",
  "com.typesafe.akka" %% "akka-http" % V.`akka-http`
)