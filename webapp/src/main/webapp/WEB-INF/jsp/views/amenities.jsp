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
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title>Neibo - <spring:message code="Reservations"/></title>
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
        </div>

        <div class="column-middle">
            <div  class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                <h2><spring:message code="MakeReservation"/></h2>
                <div class="divider"></div>

                <form:form method="post" action="amenities" modelAttribute="reservationForm">
                    <div class="col-md-12">
                        <form:label path="amenityId" class="mt-3 mb-1"><spring:message code="SelectAmenity"/></form:label>
                        <form:select path="amenityId" id="amenityId" required="true" class="cool-input">
                            <c:forEach var="entry" items="${amenities}">
                                <form:option value="${entry.amenityId}">
                                    <c:out value="${entry.name}" />
                                </form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="amenityId" cssClass="error" element="p"/>
                    </div>

                    <div class="col-md-12">
                        <form:label path="date" class="mt-3 mb-1"><spring:message code="ChooseDate"/></form:label>
                        <form:input path="date" id="date" type="date" required="true" class="cool-input ml-1 mb-1"/>
                        <form:errors  path="date" cssClass="error" element="p"/>
                    </div>

                    <div class="col-md-12">
                        <div class="d-flex justify-content-end m-t-40">
                            <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg w-25 font-weight-bold" style="height:40px;" ><spring:message code="SeeTimesButton"/></button>
                        </div>
                    </div>
                </form:form>

            </div>

            <!-- Include the page selector -->
            <c:if test="${totalPages >  1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}" />
                    <jsp:param name="totalPages" value="${totalPages}" />
                </jsp:include>
            </c:if>

            <%@ include file="/WEB-INF/jsp/components/widgets/amenitiesSchedule.jsp" %>

            <!-- Include the page selector -->
            <c:if test="${totalPages >  1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}" />
                    <jsp:param name="totalPages" value="${totalPages}" />
                </jsp:include>
            </c:if>
        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
            <%@ include file="/WEB-INF/jsp/components/widgets/amenitiesRightColumn.jsp" %>
        </div>

        <c:if test="${param.showSuccessMessage == true}">
            <c:set var="successMessage">
                <spring:message code="Reservation.created.successfully"/>
            </c:set>

            <jsp:include page="/WEB-INF/jsp/components/widgets/successDialog.jsp" >
                <jsp:param name="successMessage" value="${successMessage}" />
            </jsp:include>
        </c:if>
        <c:if test="${param.showErrorMessage == true}">
            <c:set var="errorMessage">
                <spring:message code="Reservation.error"/>
            </c:set>

            <jsp:include page="/WEB-INF/jsp/errors/errorDialog.jsp" >
                <jsp:param name="errorMessage" value="${errorMessage}" />
            </jsp:include>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>
</html>
