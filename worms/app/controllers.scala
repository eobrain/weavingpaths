/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

package controllers

import models.{ LatLon, Anon, Event }
import service.store.Store
import play.Logger
import play.data.validation.{ Required, Validation }
import play.mvc.Controller
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
    
    Event(latLon, acc, System.currentTimeMillis, Anon(anonid)  )
    <p>OK</p>
  }

  /** AJAX GET /application/dataForDisplay?lat=...&lon=...  (lat and lon in radians)
   * return JSON string:
   * [
   *   {anonid:..., track:[ [[lat,lon],acc,time], [[lat,lon],acc,time], [[lat,lon],acc,time], ...]}
   *   {anonid:..., track:[ [[lat,lon],acc,time], [[lat,lon],acc,time], [[lat,lon],acc,time], ...]}
   *   {anonid:..., track:[ [[lat,lon],acc,time], [[lat,lon],acc,time], [[lat,lon],acc,time], ...]}
   *   ...
   * ]
   * where each lat and lon are in radians, acc is in meters, and time is a UNIX-epoch long integer
   * */
  def dataForDisplay(anonid:String) = {

    //First find all locations for this user
    val user = Anon(anonid)
    val userEvents = Event.findAll(user)
    
    //Find all other users that are closest at some point to this user's events
    for( event <- userEvents ){
      Event.findAllNear(event.loc)
      //...
    }
  }

  /** AJAX POST /application/updateLocation
   * returns id as String
   */
  def createNewUser() =  Anon.createNew.toString

  /** AJAX POST /application/updateLocation
   *  anonid=...
   * returns status
   */
  def removeAllData(anonid:String) = {
    new Anon(anonid).removeAllData()
    <p>OK</p>
  }

}
