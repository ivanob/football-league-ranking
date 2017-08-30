import akka.actor.{Actor, ActorSystem, Props}
import TableRankingActor.CalculateTable

object Main extends App {

  override def main(args: Array[String]): Unit = {
    println("Hello world")
    val system = ActorSystem("football-Akka")
    val tableActor = system.actorOf(Props[TableRankingActor], "table-ranking")
    tableActor ! CalculateTable
  }

}
