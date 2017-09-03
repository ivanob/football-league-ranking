package beans


case class Team(id:Int, name:String, points:Int, goalsScored:Int, goalsConceded:Int, players:List[Player] = Nil){
  def averageAge:Float = {players.map(x => x.age).reduce(_+_)/players.length.toFloat}
}
