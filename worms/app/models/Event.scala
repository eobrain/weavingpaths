package models

import service.store.Store
import Event.collection
import com.mongodb.casbah.Imports._

case class Event(loc:LatLon, acc:Double, time:Long, anon:Anon){
  val event = MongoDBObject("loc" -> loc.toList,
			    "acc" -> acc,
			    "time" -> System.currentTimeMillis,
			    "anonid" -> anon.toString
			  )
  collection += event
  
}

object Event{

  private val collection = Store.db("Event")

  //Allow geo indexing and indexing by user
  collection ensureIndex MongoDBObject("loc" -> "2d", "anonid" -> 1)

  private def makeEvent(obj:DBObject) = {
      val latLonObj = obj.as[DBObject]("loc")
      val latLon = LatLon( latLonObj.as[Double]("lat"), latLonObj.as[Double]("lon") )
      val anon = Anon( obj.as[String]("anonid") )							    
      Event( latLon, obj.as[Double]("acc"), obj.as[Long]("time"), anon )
    }

  def findAll(anon:Anon) = 
    (collection find MongoDBObject("anonid" -> anon.toString))  map makeEvent

  def findAllNear(latLon:LatLon) = 
    (collection find MongoDBObject("loc" ->  
			      MongoDBObject("$nearSphere" -> latLon.toList)
			    ) )  map makeEvent

  def findOneNear(latLon:LatLon) = 
    (collection findOne MongoDBObject("loc" ->  
			      MongoDBObject("$nearSphere" -> latLon.toList)
			    ) )  map makeEvent

  /** Remove all data for this user */
  def removeAllData(anon:Anon){
    collection.remove( MongoDBObject("anonid" -> anon.toString) )
  }

  /** Remove all event data for all users.  Warning: do not use this in production */
  def removeAllData(){
    collection.remove( MongoDBObject() )
  }
  

}
