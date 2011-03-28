/*
 * Copyright (c) 2010 Eamonn O'Brien-Strain, eob@well.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 */

package models

import scala.collection.mutable.HashMap
import math.{ Pi, sqrt, sin, cos, atan2 }
import LatLon._

/** A point on the surface of the earth, with latitute and longitude in radians */
case class LatLon(lat:Double, lon:Double){

  //Latitude must be between -90 degrees and +90 degrees 
  require(-Pi/2 <= lat && lat <= Pi/2)
  //Longitude must be between -180 degrees and +180 degrees 
  require(-Pi   <= lon && lon <= Pi  )

  def toList = List(lat,lon)

  /** @return distance in meters from another point */
  def metersFrom(that:LatLon) = {
    val sinHalfDLat = sin((that.lat - this.lat) / 2)
    val sinHalfDLon = sin((that.lon - this.lon) / 2)
    val a = sinHalfDLat * sinHalfDLat + sinHalfDLon * sinHalfDLon * cos(this.lat) * cos(that.lat)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a)); 
    EARTH_RADIUS * c;      
  }

}

object LatLon {
  private val EARTH_RADIUS = 6367500; // meters
  private val RADIANS_PER_DEGREE = Pi / 180

  def radians(degrees:Double) = degrees * RADIANS_PER_DEGREE
}


/** Reduce the dimensionality of a set of 2-D latlon points to a set of 1-D points on a line. */
class LatLonsAsLine(center:LatLon, latLons:Iterable[LatLon]) {

  val distMatrix = new HashMap[(LatLon,LatLon),Double]
  for( a <- latLons ) 
    for( b <- latLons ) 
      distMatrix( (a,b) ) = a metersFrom b
  val map = new HashMap[LatLon,Double]
  map(center) = 0
  for(latLon <- latLons)
    if( !(map contains latLon) ){
      val dist = distMatrix( (center,latLon) )

      //Try putting point on negative side
      map(latLon) = -dist
      val negRms = rms

      //Tru putting point on positive side
      map(latLon) = dist
      val posRms = rms

      if( negRms < posRms )
	//if neg was better use neg
	map(latLon) = -dist
      //otherwise leave as pos
      
    }
      
  /** root-mean-square error of current allocation */
  private def rms = {
    var sumSq = 0.0
    for((latLonA,posnA) <- map)
      for((latLonB,posnB) <- map){
	val correctDistance = distMatrix( (latLonA,latLonB) )
	val distortedDistance = (posnA-posnB).abs
	val diff = correctDistance-distortedDistance
	sumSq += (diff*diff)
      }
    sqrt(sumSq/map.size)
  }

}
