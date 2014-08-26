<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<html>
<jsp:include page="partial/head.jsp" flush="true">
    <jsp:param name="pageTitle" value="Movie Location Database | San Francisco | Uber Code Challenge"/>
</jsp:include>

<body class="home">
  
<div id="container" class="clearfix">
  <div id="map-canvas" class="home-map"></div>  
</div>
<script type="text/javascript">

$(window).load( function(){

});  

</script>

<script type="text/javascript">
      function initialize() {
        var mapOptions = {
          center: new google.maps.LatLng(38.8756844,-104.7583595),
          zoom: 3,
          disableDefaultUI: true
        };
        var map = new google.maps.Map(document.getElementById("map-canvas"),
            mapOptions);
      }
      google.maps.event.addDomListener(window, 'load', initialize);
</script>


</body></html>