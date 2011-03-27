/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

import store.Store
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
    val latOrig = Math.random -0.5
    val lonOrig = Math.random - 0.5
    val accOrig = Math.random * 100

    val response = POST(
      "/application/updateLocation",
      Map("lat" -> latOrig.toString,
	  "lon" -> lonOrig.toString, 
	  "acc" -> accOrig.toString
	).asJava,
      Map[String,File]().asJava
    )
    assertIsOk(response)
    assertContentEquals("<p>OK</p>",response)

    val query = MongoDBObject("loc" ->  
			      MongoDBObject("$nearSphere" -> List(latOrig, lonOrig))
			    ) 
    Store.locations findOne query match {
      case Some(result) =>
	Logger.info("loc=%s",result)
        var loc = result.as[BasicDBList]("loc")
        var lat:Double = loc(0).asInstanceOf[Double]
        var lon:Double = loc(1).asInstanceOf[Double]
	assertEquals(latOrig, lat, epsilon)
	assertEquals(lonOrig, lon, epsilon)
	assertEquals(accOrig, result.as[Double]("acc"), epsilon)
        assertTrue(result.as[String]("sess").length > 10)
      case None =>
	fail
    }
  } 
  
  val epsilon:Double = 0.001
}
