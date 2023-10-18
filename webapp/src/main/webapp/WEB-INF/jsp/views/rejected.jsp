<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <title><spring:message code="Welcome.to.neibo"/></title>
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/landingPage.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>
<body>
<%----%>

<%@ include file="/WEB-INF/jsp/components/structure/backgroundDrawing.jsp" %>
<div class="container">
    <div class="d-flex flex-column justify-content-center align-items-center ">
        <div class="cool-static-container" style="width: 500px; margin-top: 100px">
            <div class="d-flex flex-column justify-content-center align-items-center " style="text-align: center">
                <p style="color: var(--primary); font-size: 30px"><spring:message code="Rejected.phrase.1"/></p><br>
                <spring:message code="Rejected.phrase.2"/> <br>

                <form:form method="post" action="rejected" modelAttribute="neighborhoodForm" id="neighborhoodForm">
                    <form:errors cssClass="error" element="p"/>
                    <div class="centered-column">
                            <%-- SELECT NEIGHBORHOOD --%>
                        <div class="form-input">
                            <form:select path="neighborhoodId" class="cool-select">
                                <c:forEach var="entry" items="${neighborhoodsList}">
                                    <form:option value="${entry.getNeighborhoodId()}"><c:out
                                            value="${entry.getName()}"/></form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <button type="submit" class="action-button"><spring:message code="Request"/></button>
                </form:form>

                <a href="${pageContext.request.contextPath}/logout" class="cool-button m-t-40"><spring:message
                        code="GoBackToMainPage"/></a>
            </div>
        </div>
    </div>
</div>
<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
</body>
</html>
