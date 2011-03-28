import org.junit.{Test, Assert}
import play.test.UnitTest
import models.LatLon
import LatLon.radians
import Math.{ Pi, random }
import Assert.{ assertEquals, assertTrue }
import LatLonTest._

class LatLonTest extends UnitTest {

  @Test
  def basicTest {
    val expected = List(radians(37.774930), radians(-122.419416))
    assertEquals( expected, sanFrancisco.toList )
  }

  @Test
  def isCertainDistanceFromAnotherLocation {
    //44.11 km +/- 0.2 km
    assertEquals(44110, sanFrancisco metersFrom paloAlto, 200); 
    // 8207.25 km +/- 200 km
    assertEquals(8207250, sanFrancisco metersFrom dublin,  200000); 
  }

  @Test
  def hasACommutativeDistanceMetric {
    for (i <- 1 to 100) {
      val a = randomLatLon
      val b = randomLatLon
      val ab = a metersFrom b
      val ba = b metersFrom a
      val epsilon = (ab+ab)/1000 //innacuracy
      assertEquals("epsilon="+epsilon, ab, ba, epsilon);
    }
  }

  @Test
  def metricObeysTriangleEnequality {
    for (i <- 1 to 100) {
      val a = randomLatLon
      val b = randomLatLon
      val c = randomLatLon
      val ab = a metersFrom b
      val ac = a metersFrom c
      val bc = b metersFrom c
      assertTrue( ab < ac+bc )
      assertTrue( ac < ab+bc )
      assertTrue( bc < ac+ab )
    }
  }

}

object LatLonTest{
  var sanFrancisco = new LatLon(radians(37.774930), radians(-122.419416))
  var paloAlto     = new LatLon(radians(37.441883), radians(-122.143020))
  var dublin       = new LatLon(radians(53.344104), radians(  -6.267494))

  def randomLatLon() = new LatLon( random*Pi-Pi/2, random*Pi*2-Pi )

}
