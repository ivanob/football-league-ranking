import beans.Player
import org.scalatest.FunSuite

class PlayerSuite extends FunSuite {

  //This test uses as local date: "2017-09-03"
  test("The age of a player is calculated correctly") {
    assert(Player("1996-04-25").age == 21)
  }
}
