package actors

import actors.TableRankingActor.CalculateTable
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString

import actors.TeamActor.GetInfoTeamPlayers
import beans.Team
import services.JsonFootballParser

class TableRankingActor extends Actor {
  import akka.pattern.pipe
  import context.dispatcher

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
          teamActor ! GetInfoTeamPlayers(t)
        })

      }
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg

}