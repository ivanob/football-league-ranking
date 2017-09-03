package beans
import org.joda.time.{LocalDate, Years}

case class Player(dateBirth: String){
  val age = {
    //Example of dateBirth: 1996-04-25
    val splitDateBirth: Array[Int] = dateBirth.split("-").map(x => Integer.parseInt(x))
    val birthdate:LocalDate = new LocalDate(splitDateBirth(0), splitDateBirth(1), splitDateBirth(2))
    val now:LocalDate = new LocalDate()
    val age:Years = Years.yearsBetween(birthdate, now)
    age.getYears
  }
}
