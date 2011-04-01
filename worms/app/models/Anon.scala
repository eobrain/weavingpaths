package models

import service.store.Store
import play.Logger
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64
import com.mongodb.casbah.Imports._

/** Anon IDs are 12-byte values (16 base64 characters) http://www.mongodb.org/display/DOCS/Object+IDs */
case class Anon(anonid:String) {
  Logger.info("Anonid(%s)", anonid)

  require(anonid.length==16)

  override def toString = anonid

  def removeAllData() { Event.removeAllData(this) }

}

/** Anonymous ID */
object Anon {

  private var collection = Store.db("Anon")

  
  /** Create a new anon */
  def createNew = {
    val obj = MongoDBObject()
    collection += obj
    Anon(Base64.encodeBase64URLSafeString(obj._id.get.toByteArray))
  }

}

