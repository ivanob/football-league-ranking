import actors.TableRankingActor
import actors.TableRankingActor.CalculateTable
import akka.actor.{Actor, ActorSystem, Props}

object Main extends App {

  override def main(args: Array[String]): Unit = {
    val system = ActorSystem("football-Akka")
    val tableActor = system.actorOf(Props[TableRankingActor], "table-ranking")
    tableActor ! CalculateTable
  }

}
