import akka.actor.{Actor, ActorSystem, Props}
import TableRankingActor.CalculateTable
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import scala.util.parsing.json._

class TableRankingActor extends Actor {
  import akka.pattern.pipe
  import context.dispatcher
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)
  val config = ConfigFactory.load()
  val url = config.getString("football-stats-api.http.url")
  val token = config.getString("football-stats-api.http.token")

  def receive = {
    case CalculateTable => {
      println("Enter in CalculateTable")
      http.singleRequest(HttpRequest(uri = url+"1181?api_token="+token)).pipeTo(self)
    }
    case HttpResponse(StatusCodes.OK, headers, entity, _) => {
      println("response received")
      entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
        println("Got response, body: " + body.utf8String)
        val obj = JSON.parseFull(body.utf8String)
        val a = 2
      }
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg

}