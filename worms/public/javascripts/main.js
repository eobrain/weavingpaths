// -*- tab-width: 4 -*-

/*jslint regexp: false, strict: true */
"use strict";
var window, $, getSuperCookie, setSuperCookie;


/** Utility class for all pages of site.*/
function WormsMain() {

	//Adapted from http://stackoverflow.com/questions/901115/get-querystring-values-in-javascript
	function getUrlParams() {
		var nameValue, 
		    urlParams = {},
		    SPACE = /\+/g,  // Regex for replacing addition symbol with a space
		    SEP = /([^&=]+)=?([^&]*)/g,
		    query = window.location.search.substring(1);

		function decode(s) {
			return decodeURIComponent(s.replace(SPACE, " ")); 
		}
		
		
		while ((nameValue = SEP.exec(query)) !== null) {
			urlParams[decode(nameValue[1])] = decode(nameValue[2]);
		}
		return urlParams;
	}

	var anonid,
	    COOKIE_NAME = "worms.anonid", 
	    urlParams = getUrlParams();

	function displayAnonid() {
		$(".anonid").html(anonid);
	}

	//find anonid
	//first try query parameter
	anonid = urlParams[COOKIE_NAME];
	if (anonid === undefined || anonid.length !== 16) {
		$(function () {
			//Then try Flash cookie
			try{
				anonid = getSuperCookie(COOKIE_NAME);
			}catch(err){
				console.log(err)
				anonid = null
			}
			if (anonid === null || anonid.length !== 16) {
				//Then try regular cookie
				anonid = $.cookie(COOKIE_NAME);
				
				if (anonid === null || anonid.length !== 16) {
					//Create a new anonid
					$.post(
						"/application/createNewUser",
						{},
						function (data) {
							anonid = data;
							
							//Store this new id
							$.cookie(COOKIE_NAME, anonid, {expires: 365 * 10});
							setSuperCookie(COOKIE_NAME, anonid);
							
							$('#main_msg').html("Created new randomly generated ID " + anonid);
							displayAnonid();
						}
						
					);
				}
			}
		});
	}
	

	$(function () { //executed after DOM loaded
		displayAnonid();
	});

	/////////////////////////////////////////////////////////////////////////////
	//Public properties
	/////////////////////////////////////////////////////////////////////////////

	this.getAnonid = function () {
		return anonid;
	};

    this.removeAllData = function () {
		$.post(
			"/application/removeAllData",
			{'anonid': anonid},
			function (data) {
				$('#main_msg').html(data);
			}
		);
	};

}

var wormsMain = new WormsMain();
