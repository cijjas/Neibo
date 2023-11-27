<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="ChooseTime"/></title>
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row init">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
        </div>

        <div class="column-middle">
            <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                <c:choose>
                    <c:when test="${empty bookings}">
                        <div>
                            <div class="f-c-c-c " style="text-align: center">
                            <span class="w-75 mb-2">
                                <spring:message code="Unavailable.times.message.1"/>
                                <span class="font-weight-bolder c-primary"><c:out value="${amenityName}"/></span>
                                <spring:message code="Unavailable.times.message.2"/>
                                <c:out value="${date}"/>.
                            </span>
                                <span class="w-75 mb-2">
                                <spring:message code="Unavailable.times.message.3"/>
                            </span>
                            </div>
                            <div class="col-md-12">
                                <div class="d-flex justify-content-center m-t-40">
                                    <a href="javascript:history.go(-1);"
                                       class="cool-button cool-small on-bg w-25 font-weight-bold"><spring:message
                                            code="Go.back"/></a>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h2><spring:message code="ChooseTime"/></h2>
                        <p class="c-light-text mt-2"><c:out value="${amenityName}"/></p>
                        <p class="c-light-text mt-2"><c:out value="${date}"/></p>
                        <div class="divider"></div>

                        <div class="f-c-c-c mt-4">
                            <div class="shifts-reservation f-c-c-c w-50">
                                <span class="font-weight-bold font-size-16">
                                    <spring:message code="Available.times"/>
                                </span>
                                <form name="shiftForm" action="${pageContext.request.contextPath}/reservation"
                                      method="post">
                                    <input type="hidden" name="amenityId" value="${amenityId}"/>
                                    <input type="hidden" name="date" value="${date}"/>


                                    <c:forEach var="shift" items="${bookings}" varStatus="loopStatus">

                                        <c:choose>
                                            <c:when test="${shift.taken}">
                                                <div class="cat">
                                                    <label class="w-100">
                                                        <input type="checkbox" name="selectedShifts"
                                                               value="${shift.shiftId}" disabled/>
                                                        <span>${shift.startTime.timeInterval} - ${shift.endTime}</span>
                                                    </label>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="cat">
                                                    <label class="w-100">
                                                        <input type="checkbox" name="selectedShifts"
                                                               value="${shift.shiftId}"/>
                                                        <span>${shift.startTime.timeInterval} - ${shift.endTime}</span>
                                                    </label>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </form>
                            </div>
                            <div class="f-c-c-c">
                                <div class="f-r-c-c" style="gap: 5px">
                                    <spring:message code="Remember.message.1"/>
                                    <span class="c-primary font-weight-bolder ">
                                    <spring:message code="Remember.message.2"/>
                                </span>
                                    <spring:message code="Remember.message.3"/>

                                </div>
                            </div>
                            <div class="col-md-12">
                                <div class="d-flex justify-content-end m-t-40">
                                    <button onclick="submitShifts()" type="submit"
                                            class="cool-button cool-small on-bg w-25 font-weight-bold"
                                            style="height:40px;"><spring:message code="Reserve"/></button>
                                </div>
                                <script>
                                    function submitShifts() {
                                        document.forms["shiftForm"].submit();
                                    }
                                </script>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
            <%@ include file="/WEB-INF/jsp/components/widgets/amenitiesRightColumn.jsp" %>
        </div>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>
</html>
