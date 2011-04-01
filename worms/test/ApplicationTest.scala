/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

import models.{ Anon, Event, LatLon }
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
import math.random
import ApplicationTest._

class ApplicationTest extends FunctionalTest {

  @Before
  def removeAllOldStuffInDb {
    Event.removeAllData()
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

    updateLocation(latOrig, lonOrig, accOrig, Anon("0123456789012345678901"))


    Event.findOneNear(LatLon(latOrig,lonOrig)) match {
      case Some(result) =>
	Logger.info("result=%s",result)
	assertEquals(latOrig, result.loc.lat, EPSILON)
	assertEquals(lonOrig, result.loc.lon, EPSILON)
	assertEquals(accOrig, result.acc, EPSILON)
        assertEquals(16, result.anon.toString.length)
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
    val anonid = Anon( getContent(createUserResponse) )

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

  def dataCount(anon:Anon) = Event.findAll(anon).length
  
  private def updateLocation(lat:Double, lon:Double, acc:Double, anon:Anon) {
    val response = POST(
      "/application/updateLocation",
      Map("lat"    -> lat.toString,
	  "lon"    -> lon.toString, 
	  "acc"    -> acc.toString,
	  "anonid" -> anon.toString
	).asJava,
      Map[String,File]().asJava
    )
    assertIsOk(response)
    assertContentEquals("<p>OK</p>", response)
  }
  
}
