package actors

import actors.TableRankingActor.{CalculateTable, ResultTeamPlayers, TableMsg}
import akka.actor.{Actor, ActorSystem, Props, ReceiveTimeout}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import actors.TeamActor.GetInfoTeamPlayers
import beans.{Player, Team}
import services.{JsonFootballParser, ScoringAlgorithm}

class TableRankingActor extends Actor {
  import akka.pattern.pipe
  import context._
  import concurrent.duration._

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
          teamActor ! GetInfoTeamPlayers(t, self)
        })
        setReceiveTimeout(3 seconds) //Timeout I will wait to receive all responses
        context.become(waitingForResponses(teams.length-1, teams))
      }
    }
  }

  //This function handles the wait for N responses from the TeamActors
  def waitingForResponses(numTeams: Int, teams: List[Team]): Receive = {
    case ResultTeamPlayers(players: List[Player], team: Team) => {
      if(numTeams == 0) {
        //System.out.println("Finished")
        val newTeam: Team = Team(team.id, team.name, team.points, team.goalsScored, team.goalsConceded, players)
        val finalTable = (teams diff List(team)):::List(newTeam)
        //Order teams following the new ranking algorithm
        val orderedTable:List[(Team,Float)] = ScoringAlgorithm.orderByRanking(finalTable)
        orderedTable.map(x => {
          System.out.println("- Team: " + x._1.name + ", Score: " + x._2)
        })
        context stop self
      }else {
        val newTeam: Team = Team(team.id, team.name, team.points, team.goalsScored, team.goalsConceded, players)
        become(waitingForResponses(numTeams-1, (teams diff List(team)):::List(newTeam)))
      }
    }
    case ReceiveTimeout => {
      context stop self
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg
  case class ResultTeamPlayers(players: List[Player], team: Team) extends TableMsg

}