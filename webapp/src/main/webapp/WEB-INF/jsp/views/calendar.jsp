<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
        <title>Neibo - <spring:message  code="Calendar"/>r</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com">
        <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
        <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
        <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    </head>

    <body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
        <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
        <div class="container">
            <div class="calendar-box">
                <%@ include file="/WEB-INF/jsp/components/calendarBox.jsp" %>
            </div>
        </div>

        <div class="container">
            <div class="events-header">
                <%@include file="/WEB-INF/jsp/components/eventsHeader.jsp"%>
            </div>
        </div>

        <div class="container">
            <div class="events-index">
                <%@include file="/WEB-INF/jsp/components/eventsIndex.jsp"%>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

        <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>
    </body>

</html>
