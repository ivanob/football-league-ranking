package actors

import actors.TableRankingActor.CalculateTable
import actors.TeamActor.GetInfoTeam
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import spray.json._
import DefaultJsonProtocol._
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
        val teams: List[Team] = JsonFootballParser.parseLeagueCall(body.utf8String)
      /*  val source = body.utf8String
        val jsonAst: JsValue = source.parseJson
       val a = jsonAst\"data"
        //val obj: Option[Any] = JSON.parseFull(body.utf8String)

        System.out.println(body.utf8String)*/

        val teamActor = system.actorOf(Props[TeamActor], "team-actor")
        teamActor ! GetInfoTeam
      }
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg

}