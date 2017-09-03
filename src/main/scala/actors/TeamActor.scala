package actors
import actors.TeamActor.GetInfoTeamPlayers
import akka.actor.Actor
import beans.Team

class TeamActor extends Actor {

  def receive = {
    case GetInfoTeamPlayers(t:Team) => {
      System.out.println("Llega al info team " + t.name)
    }
  }
}

object TeamActor {
  sealed trait TeamMsg
  case class GetInfoTeamPlayers(t:Team) extends TeamMsg
}
