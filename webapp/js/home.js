//GLOBALS
var theMap;
var gmarkers = [];


function initialize() {
    var mapOptions = {
      center: new google.maps.LatLng(37.7648926,-122.4395741),
      zoom: 12,
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

	var mark = new google.maps.Marker({
	        position: new google.maps.LatLng(lat, lng),
	        map: theMap,
	        icon: thePin
	        });
	
	gmarkers.push(mark);
}      

function clearAllPins(){
	
	$(".drop-down").empty();
	$(".drop-down").hide();
	
	for(var i=(gmarkers.length)-1;i>=0;i--){
		gmarkers[i].setMap(null);
		gmarkers.pop();
	}

	gmarkers = [];
}
 
function setupListeners(){
	
	$("#main-search").keyup(function(){
		
		if($("#main-search").val().length > 2){
			acRequest($("#main-search").val());
		}else{
			$(".drop-down").hide();
		}		
	});
}

function acRequest(val){
	callAPI(urlBase+"/autocomplete/"+val, "acResponse");
}

function exactSearch(val){
	
	$.post( urlBase+"/exactSearch.json", { search: val })
	  .done(function( response ) {
	    //alert( "search results: " + response.data.hits.hits[0]._source.title );
		  
		  if(	response.result == "SUCCESS" &&
		  		response.data &&
		  		response.data.hits &&
		  		response.data.hits.hits &&
		  		response.data.hits.hits.length > 0){
			  
			  setAllPins(response.data.hits.hits);
		  }
	  });	
}

function setAllPins(hits){
	
	clearAllPins();
	var lats=[], lngs=[], x=0;
	
	for(var i=0;i<hits.length;i++){
		setNewPin(hits[i]._source.lat,hits[i]._source.lng);
		lats.push(hits[i]._source.lat);
		lngs.push(hits[i]._source.lng);
	}

	latTot = 0
	while(x<lats.length){
		latTot += parseFloat(lats[x]);
		//latTot = latTot.toFixed(7);
		x++;
	}
	x=0;
	lngTot = 0;
	while(x<lngs.length){
		lngTot +=parseFloat(lngs[x]);
		//lngTot = lngTot.toFixed(7);
		x++;
	}
	
	var latAvg = (latTot/lats.length).toFixed(7);
	var lngAvg = (lngTot/lngs.length).toFixed(7);
	
	theMap.setCenter(new google.maps.LatLng(latAvg,lngAvg));
	//theMap.setZoom(10); 
}

function acResponse(response){
	$(".drop-down").empty();
	$(".drop-down").show();
	
	if(	response.data.suggest && 
		response.data.suggest.length == 1 && 
		response.data.suggest[0].options && 
		response.data.suggest[0].options.length > 0){
		
		var htmlSpan = "";
		var dropHeight = 20*(response.data.suggest[0].options.length)+15;
		
		for(var i=0;i<response.data.suggest[0].options.length; i++){
			//alert(response.data.suggest[0].options[i].text);
			htmlSpan += "<span class=\"suggest-row\">"+response.data.suggest[0].options[i].text+"</span>";
		}
		$(".drop-down").height(dropHeight);
		$(".drop-down").html(htmlSpan);
		
		$(".suggest-row").hover(
			  function() {
			    $( this ).css("background-color", "#dfcfcf" );
			  }, function() {
				  $( this ).css("background-color", "white" );
			  }
		);
		
		$(".suggest-row").click(function(){
			exactSearch($(this).text());
		})
		
	}else{
		$(".drop-down").hide();
	}
}


$(window).load( function(){
	  initialize();
	  setupListeners();
}); 