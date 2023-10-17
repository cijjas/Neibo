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
    <link href="${pageContext.request.contextPath}/resources/css/service.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">


<div style=" position: absolute; width: 100%; height: 100%; top:0; left:0; z-index: 1">

    <%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
    <div class="container" >

        <div class="row">
            <div class="column-left">
                <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
            </div>
            <div class="column-middle">
                <div class="f-c-c">
                    <c:choose>
                        <c:when test="${otherNeighborhoods.isEmpty()}">
                            <p><spring:message code="No.new.neighborhoods"/></p>
                        </c:when>
                        <c:otherwise>
                            <form:form id="neighborhood-form" action="${pageContext.request.contextPath}/services/neighborhoods" method="post" modelAttribute="neighborhoodForm">
                                <spring:message code="Neighborhood" var="neighborhoodPlaceholder"/>
                                <form:select id="neighborhood-select" path="neighborhoodId" class="cool-input" placeholder="${neighborhoodPlaceholder}">
                                    <c:forEach var="entry" items="${otherNeighborhoods}">
                                        <form:option value="${entry.neighborhoodId}">${entry.name}</form:option>
                                    </c:forEach>
                                </form:select>
                                <div class="d-flex justify-content-end m-t-40 ">
                                    <button type="submit" class="cool-button cool-small on-bg w-25" style="height:40px;" ><spring:message code="Request"/></button>
                                </div>
                            </form:form>
                        </c:otherwise>
                    </c:choose>

                </div>

                <div class="grey-container">
                    <c:choose>
                        <c:when test="${associatedNeighborhoods.isEmpty()}">
                            <p><spring:message code="No.associated.neighborhoods"/></p>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="entry" items="${associatedNeighborhoods}">
                                <div class="cool-static-container">
                                    <p><c:out value="${entry.name}"/></p>
                                    <a href="${pageContext.request.contextPath}/services/neighborhoods/remove/${entry.neighborhoodId}" class="btn btn-link">
                                        <i class="fas fa-trash" style="color: var(--error);"></i>
                                    </a>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>

                </div>

            </div>
            <div class="column-right">
                <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

    <%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
</div>

</body>

</html>