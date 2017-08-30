import akka.actor.{Actor, ActorSystem, Props}
import TableRankingActor.CalculateTable
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}

class TableRankingActor extends Actor {
  import akka.pattern.pipe
  import context.dispatcher
  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val http = Http(context.system)

  def receive = {
    case CalculateTable => {
      println("Enter in CalculateTable")
      http.singleRequest(HttpRequest(uri = "http://akka.io")).pipeTo(self)
    }
    case HttpResponse(StatusCodes.OK, headers, entity, _) => {
      println("response received")
      println(headers)
      println(entity)
    }
  }
}

object TableRankingActor {
  sealed trait TableMsg
  case object CalculateTable extends TableMsg

}