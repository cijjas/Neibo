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
    <title>Neibo - ${channel}</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
</head>

<div class="container" >

    <div class="row">

        <script>
            function showPostsAfterDelay() {
                document.getElementById('placeholder-posts-container').style.display = 'none';
                document.getElementById('actual-posts-container').style.display = 'block';
            }

            setTimeout(showPostsAfterDelay, 1000);

        </script>

        <div id="placeholder-posts-container">
            <c:forEach begin="1" end="10" var="index">
                <%@ include file="/WEB-INF/jsp/components/placeholderBlogpost.jsp" %>
            </c:forEach>
        </div>


        <div id="actual-posts-container">
            <c:choose>
                <c:when test="${empty eventList}">
                    <div class="no-posts-found">
                        <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                        <spring:message code="Events.notFound"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="event" items="${eventList}" >
                        <jsp:include page="/WEB-INF/jsp/components/event.jsp" >
                            <jsp:param name="eventID" value="${event.eventId}" />
                            <jsp:param name="eventName" value="${event.name}"/>
                            <jsp:param name="eventDescription" value="${event.description}"/>
                            <jsp:param name="eventDate" value="${event.date}"/>
                            <jsp:param name="eventDuration" value="${event.duration}"/>
                        </jsp:include>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>

</html>