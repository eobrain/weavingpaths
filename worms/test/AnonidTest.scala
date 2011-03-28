
import models.Anonid
import play.test.UnitTest
import org.junit.{ Assert, Test }
import Assert.{ assertEquals, assertFalse }

class AnonidTest extends UnitTest {

  @Test
  def basicTest {
    val a = Anonid.random
    val b = Anonid.random
    assertFalse( a == b )

    val aCopy = Anonid(a.toString)
    assertEquals( a, aCopy )

  }

}
