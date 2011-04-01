
import models.Anon
import play.test.UnitTest
import org.junit.{ Assert, Test }
import Assert.{ assertEquals, assertFalse }

class AnonidTest extends UnitTest {

  @Test
  def basicTest {
    val a = Anon.createNew
    val b = Anon.createNew
    assertFalse( a == b )

    val aCopy = Anon(a.toString)
    assertEquals( a, aCopy )

  }

}
