package models

import play.Logger
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64

case class Anonid(anonid:String) {
  Logger.info("Anonid(%s)", anonid)

  require(anonid.length==22)

  override def toString = anonid

}

/** Anonymous ID */
object Anonid {

  private val BYTE_COUNT = 128 / 8
  private val RND = new SecureRandom
  
  /** Create a new random ID */
  def random = {
    val bytes = new Array[Byte](BYTE_COUNT)
    RND nextBytes bytes
    Anonid(Base64.encodeBase64URLSafeString(bytes))
  }

}

