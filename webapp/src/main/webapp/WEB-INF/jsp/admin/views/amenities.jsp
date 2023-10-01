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
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="CreatePost.title"/></title>
</head>


<body class="body">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle">
            <div class="upper-feed-buttons-box m-b-20">
                <a class="cool-feed-button" href="${pageContext.request.contextPath}/admin/createAmenity">
                    <spring:message code="CreateNewAmenity.button"/>
                    <i class="fa-solid fa-plus"></i>
                </a>
            </div>
            <c:forEach var="amenityWithHours" items="${amenitiesHours}">
                <div  class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                    <div class="row">
                        <div class="col-md-8">
                            <h2><c:out value="${amenityWithHours.amenity.name}" /></h2>
                        </div>
                        <div class="col-md-4 text-right">
                            <a href="${pageContext.request.contextPath}/admin/deleteAmenity/${amenityWithHours.amenity.amenityId}" class="btn btn-link">
                                <i class="fas fa-trash" style="color: darkred;"></i>
                            </a>
                        </div>
                    </div>
                    <p class="mb-3" style="color:var(--lighttext);"><c:out value="${amenityWithHours.amenity.description}" /></p>

                    <div class="d-flex flex-column justify-content-center align-items-center w-100">
                        <table class="table-striped w-100">
                            <tr>
                                <th class="day"><spring:message code="Day"/></th>
                                <th><spring:message code="Open"/></th>
                                <th><spring:message code="Close"/></th>
                            </tr>
                            <c:forEach var="day" items="${amenityWithHours.amenityHours}">
                                <tr>
                                    <td class="day"><c:out value="${day.key}"/></td>
                                    <td><c:out value="${day.value.openTime}"/></td>
                                    <td><c:out value="${day.value.closeTime}"/></td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/calendarWidget.jsp" %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
