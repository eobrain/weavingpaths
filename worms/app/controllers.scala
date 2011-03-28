/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

package controllers

import models.{ LatLon, Anonid }
import service.store.Store
import play._
import play.data.validation.{ Required, Validation }
import play.mvc._
import com.mongodb.casbah.Imports._
import Logger.info

object Application extends Controller {
    

  /** GET / */
  def index = Template

  /** GET /application/colophon */
  def colophon = Template

  /** AJAX POST /application/updateLocation
   *  lat=...&lon=...&acc=...&anonid=...
   * return status
   */
  def updateLocation(lat:Double, lon:Double, acc:Double, anonid:String) = {
    info("updateLocation("+lat+","+lon+","+acc+",%s)", anonid);
    require( anonid != null )

    val latLon = LatLon(lat,lon)
    
    val loc = MongoDBObject("loc" -> latLon.toList,
			    "acc" -> acc,
			    "time" -> System.currentTimeMillis,
			    "anonid" -> Anonid(anonid).toString
			  )
    Store.locations += loc
    <p>OK</p>
  }

  /** AJAX POST /application/updateLocation
   * returns id as String
   */
  def createNewUser() =  Anonid.random.toString

  /** AJAX POST /application/updateLocation
   *  anonid=...
   * returns status
   */
  def removeAllData(anonid:String) = {
    Store.locations.remove( MongoDBObject("anonid" -> Anonid(anonid).toString) )
    <p>OK</p>
  }

}
