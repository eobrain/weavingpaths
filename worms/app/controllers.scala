package controllers

import store.Store
import play._
import play.mvc._
import com.mongodb.casbah.Imports._

object Application extends Controller {
    

  def index = Template

  def updateLocation(lat:Double, lon:Double, acc:Double) = {
    Logger.info("updateLocation(%s,%s,%s)", lat.asInstanceOf[AnyRef], lon.asInstanceOf[AnyRef], acc.asInstanceOf[AnyRef]);
    val sessId = session.getId

    val loc = MongoDBObject("loc" -> MongoDBObject("lat" -> lat, "lon" -> lon),
			    "acc" -> acc,
			    "sess" -> sessId
			  )
    Store.locations += loc
    <p>OK</p>
  }
}
