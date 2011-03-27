package store

import com.mongodb.casbah.Imports._

object Store{

  //TODO: configure to connect to any host
  val connection = MongoConnection() //("localhost", 27017)
  val db = connection("worms")
  val locations = db("locations")
  locations ensureIndex MongoDBObject("loc" -> "2d")

}
