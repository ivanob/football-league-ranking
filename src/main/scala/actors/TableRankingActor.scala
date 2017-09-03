package actors

import actors.TableRankingActor.{CalculateTable, ResultTeamPlayers, TableMsg}
import akka.actor.{Actor, ActorSystem, Props, ReceiveTimeout}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import actors.TeamActor.GetInfoTeamPlayers
import beans.{Player, Team}
import services.JsonFootballParser

class TableRankingActor extends Actor {
  import akka.pattern.pipe
  import context._
  import concurrent.duration._
  val DELAY_BETWEEN_CALLS_MS = 400

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)

  val system = ActorSystem("football-Akka")

  def receive = {
    case CalculateTable => {
      //val configuration = ConfigFactory.load()
      //val url = configuration.getString("football-stats-api.http.url")
      //val token = configuration.getString("football-stats-api.http.token")
      val url = "https://api.soccerama.pro/v1.2/standings/season/"
      val token = "HOLCAStI6Z0OfdoPbjdSg5b41Q17w2W5P4WuoIBdC66Z54kUEvGWPIe33UYC"
      http.singleRequest(HttpRequest(uri = url+"1181?api_token="+token)).pipeTo(self)
    }
    case HttpResponse(StatusCodes.OK, headers, entity, _) => {
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        val source = body.utf8String
        val teams: List[Team] = JsonFootballParser.parseLeagueCall(source)
        //For each team, I have to retrieve the list of ages of its players
        teams.map((t:Team) => {
          val teamActor = system.actorOf(Props[TeamActor])
          Thread.sleep(DELAY_BETWEEN_CALLS_MS)
          teamActor ! GetInfoTeamPlayers(t)
        })
        setReceiveTimeout(3 seconds) //Timeout I will wait to receive all responses
        context.become(waitingForResponses(teams.length))
      }
    }
  }

  //This function handles the wait for N responses from the TeamActors
  def waitingForResponses(numTeams: Int): Receive = {
    case ResultTeamPlayers(players: List[Player]) => {
      System.out.println("ARRIVED")
      //context stop self
    }
    case ReceiveTimeout => {
      context stop self
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg
  case class ResultTeamPlayers(players: List[Player]) extends TableMsg

}