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



<body  class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">
    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle" >
            <div class="cool-static-container" >
                <h2 class="card-title"><spring:message code="CreateNewAmenity.button"/></h2>
                <div class="divider"></div>
                <div>
                    <form:form method="post" action="createAmenity" modelAttribute="amenityForm">
                        <form:errors cssClass="error" element="p"/>
                        <div class="d-flex flex-column justify-content-between align-items-center">

                            <div class="form-row">
                                <spring:message code="Name" var="namePlaceholder"/>
                                <form:input path="name" class="cool-input" placeholder="${namePlaceholder}"/>
                                <div class="form-row form-error">
                                    <form:errors path="name" cssClass="error" element="p"/>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex flex-column justify-content-between align-items-center">
                            <spring:message code="Description" var="descriptionPlaceholder"/>
                            <form:textarea path="description" class="cool-input" rows="5" placeholder="${descriptionPlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="description" cssClass="error" element="p"/>
                            </div>
                        </div>
                        <p class="m-t-40"> <spring:message code="AmenityHours"/> </p>
                        <div class="divider"></div>
                        <div class="d-flex flex-column" style="margin-top: 1rem;">
                            <!-- First 5 days -->
                            <div class="d-flex flex-row m-b-20 justify-content-between align-items-center">
                                <c:forEach var="day" items="${daysOfWeek}" varStatus="loop">
                                    <c:if test="${loop.index < 4}">
                                        <div class="form-column" style="width: 100px">
                                            <label><c:out value="${day}"/></label>
                                            <select name="${day}OpenTime" class="cool-input" style="font-size: 12px; ">
                                                <c:forEach var="time" items="${timeList}">
                                                    <option value="${time}"><c:out value="${time}"/></option>
                                                </c:forEach>
                                            </select>
                                            <select name="${day}CloseTime" class="cool-input" style="font-size: 12px; ">
                                                <c:forEach var="time" items="${timeList}">
                                                    <option value="${time}">${time}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>

                            <!-- Weekend days (6th and 7th day) -->
                            <div class="d-flex flex-row justify-content-around align-items-center">
                                <c:forEach var="day" items="${daysOfWeek}" varStatus="loop">
                                    <c:if test="${loop.index >= 4}">
                                        <div class="form-column" style="width: 100px">
                                            <label>${day}</label>
                                            <select name="${day}OpenTime" class="cool-input" style="font-size: 12px">
                                                <c:forEach var="time" items="${timeList}">
                                                    <option value="${time}">${time}</option>
                                                </c:forEach>
                                            </select>
                                            <select name="${day}CloseTime" class="cool-input" style="font-size: 12px">
                                                <c:forEach var="time" items="${timeList}">
                                                    <option value="${time}">${time}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>



                        <%--Submit button --%>
                        <div class="d-flex justify-content-end m-t-40">
                            <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg" style="height:40px;" ><spring:message code="Create.verb"/></button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
        <div class="column-right" >
            <%@ include file="/WEB-INF/jsp/components/calendarWidget.jsp" %>
        </div>
    </div>
</div>




<!-- Bootstrap JS and jQuery -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
