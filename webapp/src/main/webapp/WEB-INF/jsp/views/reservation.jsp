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
    <title>Neibo - <spring:message code="ChooseTime"/></title>
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
                <h2><spring:message code="ChooseTime"/></h2>
                <p style="color: var(--lighttext)"><c:out value="${amenityName}"/></p>
                <p style="color: var(--lighttext)"><c:out value="${date}"/></p>
                <div class="divider"></div>

                <form:form method="post" action="reservation" modelAttribute="reservationTimeForm">
                    <div class="d-flex flex-row justify-content-center align-items-center">
                        <div class="col-md-6">
                            <form:label path="startTime" class="mt-3 mb-1"><spring:message code="StartTime"/></form:label>
                            <form:select path="startTime" id="startTime" required="true" class="cool-input">
                                <c:forEach var="time" items="${timeList}">
                                    <option value="${time}">${time}</option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="startTime" cssClass="error" element="p"/>
                        </div>
                        <div class="col-md-6">
                            <form:label path="endTime" class="mt-3 mb-1"><spring:message code="EndTime"/></form:label>
                            <form:select path="endTime" id="endTime" required="true" class="cool-input">
                                <c:forEach var="time" items="${timeList}">
                                    <option value="${time}">${time}</option>
                                </c:forEach>
                            </form:select>
                            <form:errors path="endTime" cssClass="error" element="p"/>
                        </div>
                    </div>
                    <form:errors cssClass="error" element="p"/>

                    <input type="hidden" name="amenityId" value="${amenityId}" />
                    <input type="hidden" name="date" value="${date}" />

                    <div class="col-md-12">
                        <div class="d-flex justify-content-end m-t-40">
                            <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg m-b-20" style="height:40px;" ><spring:message code="Reserve"/></button>
                        </div>
                    </div>
                </form:form>
                <table class="table-striped w-100 table-hover">
                    <thead>
                    <tr>
                        <th><spring:message code="UnavailableTimes"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="time" items="${timeList}">
                        <c:set var="isUnavailable" value="false" />
                        <c:forEach var="reservation" items="${reservationsList}">
                            <c:if test="${time == reservation.startTime || time == reservation.endTime}">
                                <c:set var="isUnavailable" value="true" />
                            </c:if>
                        </c:forEach>
                        <tr>
                            <c:choose>
                                <c:when test="${isUnavailable}">
                                    <td style="color: var(--error)">
                                        <c:out value="${time}" />
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <c:out value="${time}" />
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
            <div class="grey-static-container m-t-40">
                <div class="column d-flex justify-content-center align-items-start">
                    <h3 class="m-b-20"><spring:message code="MyReservations"/></h3>
                    <c:forEach var="reservation" items="${reservationsList}">
                        <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                            <div class="f-c-c-c">
                                <div class="f-r-sb-c w-100">
                                    <h5><c:out value="${reservation.amenity.name}" /></h5>
                                    <a href="${pageContext.request.contextPath}/delete-reservation/${reservation.reservationId}" class="f-c-c-c">
                                        <i class="fas fa-trash" style="color: var(--error);"></i>
                                    </a>
                                </div>
                                <div>
                                    <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="Date"/> <c:out value="${reservation.date}" /></h6>
                                    <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="StartTime"/> <c:out value="${reservation.startTime}" /></h6>
                                    <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="EndTime"/> <c:out value="${reservation.endTime}" /></h6>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>
</html>
