package services

import beans.Team

class ScoringAlgorithm

object ScoringAlgorithm {
  def orderByRanking(teams: List[Team]): List[(Team,Float)] = {
    val totalAgeAverage = calculateTotalAgeAverage(teams)
    val table = for{
      t <- teams
    }yield((t,calculateScore(t,totalAgeAverage)))
    table.sortBy(x => x._2)
  }

  def calculateTotalAgeAverage(teams: List[Team]): Float = {
    teams.map((t:Team)=>t.averageAge).reduce(_+_)/teams.length.toFloat
  }

  def calculateScore(t: Team, totalAgeAverage: Float): Float = {
    val goalsConceded = if(t.goalsConceded==0) 1F else t.goalsConceded.toFloat
    t.points * (t.goalsScored/goalsConceded) * (t.averageAge/totalAgeAverage.toFloat)
  }
}