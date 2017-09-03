package services

import beans.{Player, Team}
import spray.json.{JsArray, JsValue}
import spray.json._
import DefaultJsonProtocol._

class JsonFootballParser

object JsonFootballParser{
  def parseLeagueCall(json: String): List[Team] = {
    val jsonAst: JsValue = json.parseJson

    val a: JsValue = (jsonAst.asJsObject).fields.get("data").get
    val b = a.asInstanceOf[JsArray].elements(0)
    val c = b.asJsObject.fields("standings")
    val d = c.asJsObject.fields.get("data").get.asInstanceOf[JsArray].elements

    d.map((x:JsValue) => {
      val stats = x.asJsObject.fields
      val points = stats("points").convertTo[Int]
      val goalsScored = stats("home_goals_attempted").convertTo[Int]
      val goalsConceded = stats("home_goals_scored").convertTo[Int]
      val id = stats("team").asJsObject.fields("id").convertTo[Int]
      val name = stats("team").asJsObject.fields("name").convertTo[String]
      Team(id,name,points,goalsScored,goalsConceded)
    }).toList
  }

  def parseTeamCall(json: String): List[Player] = {
    val jsonAst: JsValue = json.parseJson

    val a: JsValue = (jsonAst.asJsObject).fields.get("data").get
    val b = a.asInstanceOf[JsArray].elements

    b.map((x:JsValue) =>{
      val stats = x.asJsObject.fields
      val dateBirth = stats("birth_date").convertTo[String]
      Player(dateBirth)
    }).toList
  }
}