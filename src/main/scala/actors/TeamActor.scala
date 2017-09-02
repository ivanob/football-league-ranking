package actors
import actors.TeamActor.GetInfoTeam
import akka.actor.Actor

class TeamActor extends Actor {

  def receive = {
    case GetInfoTeam => {
      System.out.println("Llega al info team")
    }
  }
}

object TeamActor {
  sealed trait TeamMsg
  case object GetInfoTeam extends TeamMsg
}
