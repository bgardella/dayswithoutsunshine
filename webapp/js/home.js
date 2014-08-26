var theMap;

function initialize() {
        var mapOptions = {
          center: new google.maps.LatLng(37.7648926,-122.4395741),
          zoom: 13,
          disableDefaultUI: true,
          disableDoubleClickZoom: true,
          draggable: false,
          scrollwheel: false
        };
        
        theMap = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
}


function setNewPin(lat, lng){
	
	var thePin = new google.maps.MarkerImage('image/pin.png',
			        new google.maps.Size(15, 22),
			        new google.maps.Point(0,0),
			        new google.maps.Point(18, 42)
			    );

	var marker1 = new google.maps.Marker({
	        position: new google.maps.LatLng(lat, lng),
	        map: theMap,
	        icon: thePin
	        });
}      
      
function setupListeners(){
	
	$("#main-search").keyup(function(){
		
		if($("#main-search").val().length > 2){
			
			sendAutocomplete($("#main-search").val());
		}		
	});
	
}      
      
function sendAutocomplete(val){
	
}


$(window).load( function(){
	  initialize();
	  setupListeners();
}); 