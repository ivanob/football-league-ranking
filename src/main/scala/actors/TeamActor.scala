package actors
import actors.TableRankingActor.ResultTeamPlayers
import actors.TeamActor.GetInfoTeamPlayers
import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import beans.{Player, Team}
import services.JsonFootballParser

class TeamActor extends Actor {
  import akka.pattern.pipe
  import context.dispatcher

  val url = "https://api.soccerama.pro/v1.2/players/team/"
  val token = "HOLCAStI6Z0OfdoPbjdSg5b41Q17w2W5P4WuoIBdC66Z54kUEvGWPIe33UYC"
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)

  def receive = {
    case GetInfoTeamPlayers(t:Team, sendTo:ActorRef) => {

      //This actor will retrieve the ages of the players for the team received
      http.singleRequest(HttpRequest(uri = url + t.id + "?api_token=" + token)).map((resp:HttpResponse) =>
        resp match {
          case HttpResponse(StatusCodes.OK, _, _, _) => {
            resp.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
              val source = body.utf8String
              val players: List[Player] = JsonFootballParser.parseTeamCall(source)
              System.out.println("FINISHED THREAD, List: " + players.length)
              sendTo ! ResultTeamPlayers(players)
            }
          }
          case HttpResponse(_, _, _, _) => {
            val r = scala.util.Random
            Thread.sleep(r.nextInt(2000))
            self ! GetInfoTeamPlayers(t, sendTo)
          }
        }
      )
    }
  }
}

object TeamActor {
  sealed trait TeamMsg
  case class GetInfoTeamPlayers(t:Team, sendTo:ActorRef) extends TeamMsg
}
