// -*- tab-width:4 -*-
/*jslint browser: true */

var Modernizr, $, geoip_latitude, geoip_longitude, count = 0;

function displayLocation(lat, lon, acc) {
	count += 1;
	$("#time").html(count + ", time=" + new Date());
	$("#lat").html(lat);
	$("#lon").html(lon);
	$("#alt").html(acc);
}

function fallbackGeo() {
	displayLocation(geoip_latitude(), geoip_longitude(), 10000);
	$("#attribution").html('Approximate IP-based location courtesy of <a href="http://www.maxmind.com/app/ip-location">Maxmind</a>');
}

$(function () {
	if (Modernizr.geolocation) {
		navigator.geolocation.watchPosition(
			function (position) {
				displayLocation(
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
