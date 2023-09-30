<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
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
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="CreatePost.title"/></title>
</head>

<body class="body">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
        </div>

        <div class="column-middle">
            <c:forEach var="amenityWithHours" items="${amenitiesHours}">
                <div  class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                    <div >
                        <h2 ><c:out value="${amenityWithHours.amenity.name}" /></h2>
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
                                    <td class="day">${day.key}</td>
                                    <td>${day.value.openTime}</td>
                                    <td>${day.value.closeTime}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div class="column-right">
            <a href="${pageContext.request.contextPath}/createAmenity" class="filter-button" >
                <spring:message code="CreateNewAmenity.button"/>
                <i class="fa-solid fa-plus"></i>
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
