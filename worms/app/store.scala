/*
 * Copyright (c) 2011 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

package store

import com.mongodb.casbah.Imports._

object Store{

  //TODO: configure to connect to any host
  val connection = MongoConnection() //("localhost", 27017)
  val db = connection("worms")
  val locations = db("locations")
  locations ensureIndex MongoDBObject("loc" -> "2d")

}
