/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

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

    //val loc = MongoDBObject("loc" -> MongoDBObject("lat" -> lat, "lon" -> lon),
    val loc = MongoDBObject("loc" -> List(lat,lon),
			    "acc" -> acc,
			    "sess" -> sessId
			  )
    Store.locations += loc
    <p>OK</p>
  }
}
