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
            <div  class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                    <h2>Make a Reservation</h2>
                    <div class="divider"></div>
                    <form:form method="post" action="amenities" modelAttribute="reservationForm">
                        <div class="col-md-12">
                            <form:label path="amenityId" class="mt-3 mb-1">Select Amenity:</form:label>
                            <form:select path="amenityId" id="amenityId" required="true" class="cool-input">
                                <form:options items="${amenitiesHours}" itemValue="amenity.amenityId" itemLabel="amenity.name"/>
                            </form:select>
                            <form:errors path="amenityId" cssClass="error" element="p"/>
                        </div>

                        <div class="col-md-12">
                            <form:label path="date" class="mt-3 mb-1">Choose a Date:</form:label>
                            <form:input path="date" id="date" type="date" required="true" class="cool-input"/>
                            <form:errors path="date" cssClass="error" element="p"/>
                        </div>

                        <div class="d-flex flex-row justify-content-center align-items-center">
                            <div class="col-md-6">
                                    <form:label path="startTime" class="mt-3 mb-1">Start Time:</form:label>
                                    <form:select path="startTime" id="startTime" required="true" class="cool-input">
                                        <c:forEach var="time" items="${timeList}">
                                            <option value="${time}">${time}</option>
                                        </c:forEach>
                                    </form:select>
                                    <form:errors path="startTime" cssClass="error" element="p"/>
                            </div>
                            <div class="col-md-6">
                                    <form:label path="endTime" class="mt-3 mb-1">End Time:</form:label>
                                    <form:select path="endTime" id="endTime" required="true" class="cool-input">
                                        <c:forEach var="time" items="${timeList}">
                                            <option value="${time}">${time}</option>
                                        </c:forEach>
                                    </form:select>
                                    <form:errors path="endTime" cssClass="error" element="p"/>
                            </div>
                        </div>
                        <form:errors cssClass="error" element="p"/>

                        <div class="col-md-12">
                            <div class="d-flex justify-content-end m-t-40">
                                <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg" style="height:40px;" ><spring:message code="Reserve"/></button>
                            </div>
                        </div>

                    </form:form>
            </div>


        <%--            <form action="${pageContext.request.contextPath}/completeReservation" method="get">--%>
<%--                <label for="amenityId">Select Amenity:</label>--%>
<%--                <select id="amenityId" name="amenityId" required>--%>
<%--                    <c:forEach var="amenityWithHours" items="${amenitiesHours}">--%>
<%--                        <option value="${amenityWithHours.amenity.amenityId}">--%>
<%--                            <c:out value="${amenityWithHours.amenity.name}" />--%>
<%--                        </option>--%>
<%--                    </c:forEach>--%>
<%--                </select>--%>

<%--                <label for="chosenDate">Choose a Date:</label>--%>
<%--                <input type="date" id="chosenDate" name="chosenDate" required>--%>

<%--                <input type="submit" value="Continue">--%>
<%--            </form>--%>

            <c:forEach var="amenityWithHours" items="${amenitiesHours}">
                <div  class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
<%--                    <a href="${pageContext.request.contextPath}/reserve/<c:out value="${amenityWithHours.amenity.amenityId}" />" style="text-decoration: none;">--%>
                        <div >
                            <h2 ><c:out value="${amenityWithHours.amenity.name}" /></h2>
                        </div>
                        <p class="mb-3" style="color:var(--lighttext);"><c:out value="${amenityWithHours.amenity.description}" /></p>


                        <div class="d-flex flex-column justify-content-center align-items-center w-100">
                            <table class="table-striped w-100">
                                <tr>
                                    <th class="day">Day</th>
                                    <th>Open</th>
                                    <th>Close</th>
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
<%--                    </a>--%>
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
