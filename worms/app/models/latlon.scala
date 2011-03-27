/*
 * Copyright (c) 2010 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

package models

import Math.Pi

/** A point on the surface of the earth, with latidute and longitude in radians */
case class LatLon(lat:Double, lon:Double){

  //Latitude must be between -90 degrees and +90 degrees 
  require(-Pi/2 <= lat && lat <= Pi/2)
  //Longitude must be between -180 degrees and +180 degrees 
  require(-Pi   <= lon && lon <= Pi  )

  /** @returns distance in meters from another point */
  def metersFrom(that:LatLon) = {
    val sinHalfDLat = Math.sin((that.lat - this.lat) / 2)
    val sinHalfDLon = Math.sin((that.lon - this.lon) / 2)
    val a = sinHalfDLat * sinHalfDLat +
	  sinHalfDLon * sinHalfDLon * Math.cos(this.lat) * Math.cos(that.lat)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); 
    LatLon.EARTH_RADIUS * c;      
  }

}

object LatLon {
  private val EARTH_RADIUS = 6367500; // meters
  private val RADIANS_PER_DEGREE = Pi / 180

  def radians(degrees:Double) = degrees * RADIANS_PER_DEGREE
}
