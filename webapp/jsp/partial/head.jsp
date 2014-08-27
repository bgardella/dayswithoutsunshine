<head>
    <title>${param.pageTitle}</title>
    
    <meta name="keywords" content="movies--san francisco--uber code challenge" />
    <meta name="description" content="SPA using the city of san francisco's movie location database" />
    
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/base.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/ui-lightness/jquery-ui-1.10.4.custom.css">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/image/favicon.ico" type="image/x-icon" />
    
    <script type='text/javascript' src="<%=request.getContextPath()%>/js/jquery-1.10.2.js"></script>
    <script type='text/javascript' src="<%=request.getContextPath()%>/js/jquery-ui-1.10.4.custom.js"></script>
    
    <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=${googleApiKey}"></script>
    <script type='text/javascript' src="<%=request.getContextPath()%>/js/jsonp.js"></script>
    
    <script type='text/javascript'>
       var context = "<%=request.getContextPath()%>";
       var urlBase = "${urlBase}";
    </script>
    <script type='text/javascript' src="<%=request.getContextPath()%>/js/home.js"></script>
</head>