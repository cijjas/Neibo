<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/22/2023
  Time: 6:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>(cantidad) - Neibo </title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>
<body>
<%----%>

<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<%@ include file="/WEB-INF/jsp/components/tooltips.jsp" %>


<div class="container" >

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
        </div>

        <div class="column-middle">
            <%@ include file="/WEB-INF/jsp/components/upperFeedButtons.jsp" %>

        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/rightColumn.jsp" %>
        </div>


    </div>
</div>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
