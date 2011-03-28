/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

import models.Anonid
import service.store.Store
import org.junit.{Before, Test, Assert}
import play.Logger
import play.test._
//import play.mvc._
import play.mvc.Http._
import FunctionalTest._
import scalaj.collection.Imports._
import com.mongodb.casbah.Imports._
import java.io.File
import Assert.{assertEquals, assertTrue, fail}
import scala.collection.mutable.HashSet
import Math.random
import ApplicationTest._

class ApplicationTest extends FunctionalTest {

  @Before
  def removeAllOldStuffInDb {
    Store.locations remove MongoDBObject()
  }

  @Test
  def indexPageWorks {
    val response = GET("/")
    assertIsOk(response)
    assertContentType("text/html", response)
    assertCharset("utf-8", response)
  }

  @Test 
  def canUpdateLocation {
    val latOrig = random -0.5
    val lonOrig = random - 0.5
    val accOrig = random * 100

    updateLocation(latOrig, lonOrig, accOrig, Anonid("0123456789012345678901"))

    val query = MongoDBObject("loc" ->  
			      MongoDBObject("$nearSphere" -> List(latOrig, lonOrig))
			    ) 
    Store.locations findOne query match {
      case Some(result) =>
	Logger.info("loc=%s",result)
        var loc = result.as[BasicDBList]("loc")
        var lat:Double = loc(0).asInstanceOf[Double]
        var lon:Double = loc(1).asInstanceOf[Double]
	assertEquals(latOrig, lat, EPSILON)
	assertEquals(lonOrig, lon, EPSILON)
	assertEquals(accOrig, result.as[Double]("acc"), EPSILON)
        assertEquals(22, result.as[String]("anonid").length)
      case None =>
	fail
    }
  }

  @Test
  def canCreateNewUser {
    val response = POST("/application/createNewUser")
    assertIsOk(response)
    val content = getContent(response)
    assertTrue(content.length > 5)
  }
  
  @Test
  def eachNewUserIsUnique {
    val ids = new HashSet[String]
    for(count <- 1 to 1000){
      val response = POST("/application/createNewUser")
      assertIsOk(response)
      val id = getContent(response)
      assertTrue(!(ids contains id))
      ids += id
    }
    assertEquals( 1000, ids.size )
  }

  @Test
  def canRemoveAllDataAboutAUser {
    val createUserResponse = POST("/application/createNewUser")
    assertIsOk(createUserResponse)
    val anonid = Anonid( getContent(createUserResponse) )

    updateLocation(random -0.5, random - 0.5, random * 100, anonid)
    updateLocation(random -0.5, random - 0.5, random * 100, anonid)
    updateLocation(random -0.5, random - 0.5, random * 100, anonid)

    assertEquals(3, dataCount(anonid))

    val removeAllDataResponse = POST(
      "/application/removeAllData",
      Map("anonid" -> anonid.toString).asJava,
      Map[String,File]().asJava
    )
    assertIsOk(removeAllDataResponse)
    assertContentEquals("<p>OK</p>",removeAllDataResponse)

    assertEquals(0, dataCount(anonid))
  }

}

object ApplicationTest{

  val EPSILON = 0.001

  def dataCount(anonid:Anonid) = 
    Store.locations.find( MongoDBObject("anonid" -> anonid.toString) ).length
  
  private def updateLocation(lat:Double, lon:Double, acc:Double, anonid:Anonid) {
    val response = POST(
      "/application/updateLocation",
      Map("lat"    -> lat.toString,
	  "lon"    -> lon.toString, 
	  "acc"    -> acc.toString,
	  "anonid" -> anonid.toString
	).asJava,
      Map[String,File]().asJava
    )
    assertIsOk(response)
    assertContentEquals("<p>OK</p>", response)
  }
  
}
