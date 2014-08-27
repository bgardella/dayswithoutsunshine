<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


<html>
<jsp:include page="partial/head.jsp" flush="true">
    <jsp:param name="pageTitle" value="Movie Location Database | San Francisco | Uber Code Challenge"/>
</jsp:include>

<body class="home">
  
<div class="home-container">

  <span class="app-title">SF Location Scout</span>
  <input id="main-search" class="search-bar"></input>
  <div class="drop-down"></div>

  <div id="map-canvas" class="home-map"></div> 
  
  <div class="movie-details"></div> 
</div>

</body></html>