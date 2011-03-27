import store.Store
import org.junit._
import play.Logger
import play.test._
import play.mvc._
import play.mvc.Http._
import FunctionalTest._
import scalaj.collection.Imports._
import com.mongodb.casbah.Imports._
import java.io.File
import Assert.{assertEquals, fail}



class ApplicationTest extends FunctionalTest {

  @Test
  def indexPageWorks {
    val response = GET("/")
    assertIsOk(response)
    assertContentType("text/html", response)
    assertCharset("utf-8", response)
  }
  
  @Test 
  def canUpdateLocation {
    val response = POST(
      "/application/updateLocation",
      Map("lat"->"0.659", "lon" -> "-2.137", "acc"->"20").asJava,
      Map[String,File]().asJava
    )
    assertIsOk(response)
    assertContentEquals("<p>OK</p>",response)

    val query = MongoDBObject("loc" ->  
			      MongoDBObject("$nearSphere" -> MongoDBObject("lat"-> 0.659, "lon" -> -2.137))
			    ) 
    Store.locations findOne query match {
      case Some(result) =>
	Logger.info("loc=%s",result)
        val loc = result.as[DBObject]("loc")
	assertEquals(0.659,  loc("lat"))
        assertEquals(-2.137, loc("lon"))
        //assertEquals("20", loc("acc"))
      case None =>
	fail
    }
  }
  
}
