
var sunshineApp = {

	//GLOBALS
	_theMap : null,
	_gmarkers : [],

	
	initialize : function(){
	    var mapOptions = {
	      center: new google.maps.LatLng(37.7648926,-122.4395741),
	      zoom: 12,
	      disableDefaultUI: true,
	      disableDoubleClickZoom: true,
	      draggable: false,
	      scrollwheel: false
	    };
	
	    this._theMap = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
	},

	setNewPin : function(lat, lng, id, title, show){
		
		var thePin = new google.maps.MarkerImage('image/pin.png',
				        new google.maps.Size(15, 22),
				        new google.maps.Point(0,0),
				        new google.maps.Point(18, 42)
				    );
	
		var mark = new google.maps.Marker({
		        position: new google.maps.LatLng(lat, lng),
		        map: sunshineApp._theMap,
		        icon: thePin,
		        animation: google.maps.Animation.DROP
		        });
		
		mark.metadata = {id: id};
		
		google.maps.event.addListener(mark, 'click', function(){
			//mark.setAnimation(google.maps.Animation.BOUNCE);
			
			callAPI(urlBase+"/byid/"+mark.metadata.id, "sunshineApp.byidResponse");
			sunshineApp.setTitle(title, mark);
		});
		
		this._gmarkers.push(mark);
		
		if(show){
			this.setTitle(title, mark);
		}
	},  

	setTitle : function(title, marker){
		var infowindow = new google.maps.InfoWindow({
			content: "<div class=\"click-title\">"+ title + "</div>",
			disableAutoPan : true
			});
		infowindow.open(sunshineApp._theMap, marker);
		
		$('.click-title').click(function(){
			$(".search-bar").val($(this).text());
			sunshineApp.searchByField("title", $(this).text());		
		});
	},

	byidResponse : function(response){
	
		var actors = "<span class=\"click-actor_1\">"+response.data._source.actor_1+"</span>";
		if(response.data._source.actor_2){
			actors += ", <span class=\"click-actor_3\">"+response.data._source.actor_2+"</span>";
		}
		if(response.data._source.actor_3){
			actors += ", <span class=\"click-actor_3\">"+response.data._source.actor_3+"</span>";
		}
		
		var metaMarkup = "<span class=\"movie-title\"><span class=\"click-title\">"+response.data._source.title+"</span> (<span class=\"click-year\">"+response.data._source.release_year+"</span>)</span>"+
						"<span class=\"movie-attr\"><b>Directed By:</b> <span class=\"click-director\">"+response.data._source.director+"</span></span>"+
						"<span class=\"movie-attr\"><b>Production Company:</b><br/> <span class=\"click-prodco\">"+response.data._source.production_company+"</span></span>"+
						"<span class=\"movie-attr\"><b>Actors:</b> "+actors+"</span>"+
						"<span class=\"movie-attr\"><b>Writer:</b> <span class=\"click-writer\">"+response.data._source.writer+"</span></span>"+
						"<span class=\"movie-attr\"><b>Location:</b> <span class=\"click-location\">"+response.data._source.locations+"</span></span>";
		
		if(response.data._source.fun_facts){
			metaMarkup += "<span class=\"movie-attr\"><b>Fun Facts:</b> "+response.data._source.fun_facts+"</span>";
			$(".movie-details").css("height", "333px" );
		}else{
			$(".movie-details").css("height", "238px" );
		}
		
		$(".movie-details").html(metaMarkup);
		$(".movie-details").show();
		
		$('.click-title').click(function(){
			$(".search-bar").val($(this).text());
			sunshineApp.searchByField("title", $(this).text());		
		});
		$('.click-year').click(function(){
			sunshineApp.searchByField("release_year", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-director').click(function(){
			sunshineApp.searchByField("director", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-prodco').click(function(){
			sunshineApp.searchByField("production_company", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-actor_1').click(function(){
			sunshineApp.searchByField("actor_1", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-actor_2').click(function(){
			sunshineApp.searchByField("actor_2", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-actor_3').click(function(){
			sunshineApp.searchByField("actor_3", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-writer').click(function(){
			sunshineApp.searchByField("writer", $(this).text());
			$(".search-bar").val($(this).text());
		});
		$('.click-location').click(function(){
			sunshineApp.searchByField("locations", $(this).text());
			$(".search-bar").val($(this).text());
		});
	},

	clearAllPins : function(){
		
		$(".movie-details").hide();
		$(".drop-down").empty();
		$(".drop-down").hide();
		this._theMap.setZoom(12);
		this._theMap.draggable = false;
		
		for(var i=(this._gmarkers.length)-1;i>=0;i--){
			this._gmarkers[i].setMap(null);
			this._gmarkers.pop();
		}
	
		this._gmarkers = [];
	},
 
	setupListeners : function(){
	
		$("#main-search").keyup(function(){
			
			if($("#main-search").val().length > 2){
				sunshineApp.acRequest($("#main-search").val());
			}else{
				$(".drop-down").hide();
			}		
		});
		
		$(".random-btn").button().click(function(){
			sunshineApp.selectRandom();
		})
		
		var downCount = 0;
		
		$(".search-bar").keydown(function(ev){
			if(ev.keyCode == 13) { //return
				sunshineApp.looseSearch(); 
			}
		});
	},

	looseSearch : function(){
		var searchString = $(".search-bar").val();
		
		$.post( urlBase+"/looseSearch.json", { search: searchString })
		  .done(function( response ) {
		    //alert( "search results: " + response.data.hits.hits[0]._source.title );
	
			  if(	response.result == "SUCCESS" &&
				  		response.data &&
				  		response.data.hits &&
				  		response.data.hits.total == "0" ){
				  alert("No results found for: \"" + searchString + "\"");
			  }
			  
			  if(	response.result == "SUCCESS" &&
			  		response.data &&
			  		response.data.hits &&
			  		response.data.hits.hits &&
			  		response.data.hits.hits.length > 0){
				  
				  setAllPins(response.data.hits.hits);
			  }
		  });	
	},

	acRequest : function(val){
		callAPI(urlBase+"/autocomplete/"+val, "sunshineApp.acResponse");
	},

	searchByField : function (fieldName, searchString){
		callAPI(urlBase+"/byField/"+fieldName+"/"+searchString, "sunshineApp.byFieldResponse");
	},
	byFieldResponse : function (response){
		if(		response.data &&
		  		response.data.hits &&
		  		response.data.hits.hits &&
		  		response.data.hits.hits.length > 0){
			  
			  this.setAllPins(response.data.hits.hits);
			  
			  //$('#main-search').val(val);
		}
	},

	selectRandom : function (){
		this.clearAllPins();
		$(".search-bar").val("");
		callAPI(urlBase+"/selectAll", "sunshineApp.randomResponse");
	},

	randomResponse : function (response){
		if(response.data.hits.hits.length > 25){
			var chunk = 25;
			var randIdx = Math.floor(Math.random()*(response.data.hits.hits.length-chunk));
	
			var myChunk = response.data.hits.hits.slice(randIdx,randIdx+chunk);
			this.setAllPins(myChunk)
	
		}else{	
			this.setAllPins(response.data.hits.hits);
		}
	},

	exactSearch : function(val){
	
		$.post( urlBase+"/exactSearch.json", { search: val.replace(/:/g, "") })
		  .done(function( response ) {
		    //alert( "search results: " + response.data.hits.hits[0]._source.title );
			  
			  if(	response.result == "SUCCESS" &&
			  		response.data &&
			  		response.data.hits &&
			  		response.data.hits.hits &&
			  		response.data.hits.hits.length > 0){
				  
				  sunshineApp.setAllPins(response.data.hits.hits);
				  
				  $('#main-search').val(val);
			  }
		  });	
	},

	setAllPins : function(hits){
		
		this.clearAllPins();
		var lats=[], lngs=[], x=0;
		
		for(var i=0;i<hits.length;i++){
			if(hits[i]._source){
				this.setNewPin(hits[i]._source.lat, hits[i]._source.lng, hits[i]._id, hits[i]._source.title, true);
				lats.push(hits[i]._source.lat);
				lngs.push(hits[i]._source.lng);
			}else if(hits[i].fields){
				this.setNewPin(hits[i].fields.lat, hits[i].fields.lng, hits[i]._id, hits[i].fields.title); 
				if(hits.length < 30){
					lats.push(hits[i].fields.lat);
					lngs.push(hits[i].fields.lng);
				}
			}
		}
	
		if(hits.length < 30){
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
			
			this._theMap.setCenter(new google.maps.LatLng(latAvg,lngAvg));
			this._theMap.setZoom(13);
			this._theMap.draggable = true;
		}
	},

	acResponse : function(response){
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
				sunshineApp.exactSearch($(this).text());
			})
			
		}else{
			$(".drop-down").hide();
		}
	},

} // end sunshineApp

$(window).load( function(){
	sunshineApp.initialize();
	sunshineApp.setupListeners();
	$("body").addClass('loaded');
}); 