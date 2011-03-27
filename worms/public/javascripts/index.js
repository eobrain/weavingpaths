// -*- tab-width:4 -*-
/*jslint browser: true */

var Modernizr, $, geoip_latitude, geoip_longitude;



$(function () {

	var count = 0, DEGREES_PER_RADIAN = 57.2957795;

	function handleLocation(lat, lon, acc) {
		var latRad = lat / DEGREES_PER_RADIAN,
		    lonRad = lon / DEGREES_PER_RADIAN;
		count += 1;
		$("#time").html(count + ", time=" + new Date());
		$("#lat").html(latRad);
		$("#lon").html(lonRad);
		$("#acc").html(acc);
		$.post(
			"/application/updateLocation",
			{'lat': latRad, 'lon': lonRad, 'acc': acc},
			function (data) {
				$('#msg').html(data);
			}
		);
	}
	
	function fallbackGeo() {
		handleLocation(geoip_latitude(), geoip_longitude(), 10000);
		$("#attribution").html('Approximate IP-based location courtesy of <a href="http://www.maxmind.com/app/ip-location">Maxmind</a>');
	}
	
	
	
	if (Modernizr.geolocation) {
		navigator.geolocation.watchPosition(
			function (position) {
				handleLocation(
					position.coords.latitude,
					position.coords.longitude,
					position.coords.accuracy
				);
			},
			function (error) {
				fallbackGeo();
			},
			{enableHighAccuracy: true}
		);  
	} else {
		fallbackGeo();
	}

});
  