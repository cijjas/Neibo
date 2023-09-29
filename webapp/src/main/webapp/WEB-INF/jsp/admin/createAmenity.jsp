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
    <title><spring:message code="CreateNewAmenity.button"/></title>
</head>

<body class="body">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">
    <div class="row">

        <div class="column-publish" >
            <div class="cool-static-container" >
                <h2 class="card-title"><spring:message code="CreateNewAmenity.button"/></h2>
                <div class="divider"></div>

                <form:form method="post" action="createAmenity" modelAttribute="amenityForm">
                    <form:errors cssClass="error" element="p"/>

                    <div class="form-column" style="margin-top:1rem;">
                        <div class="form-group">

                            <div class="form-row">
                                <spring:message code="Name" var="namePlaceholder"/>
                                <form:input path="name" class="cool-input" placeholder="${namePlaceholder}"/>
                                <div class="form-row form-error">
                                    <form:errors path="name" cssClass="error" element="p"/>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <spring:message code="Description" var="descriptionPlaceholder"/>
                            <form:textarea path="description" class="cool-input" rows="5" placeholder="${descriptionPlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="description" cssClass="error" element="p"/>
                            </div>
                        </div>

                        <p> <spring:message code="AmenityHours"/> </p>
                        <div class="form-row">
                            <spring:message code="OpeningClosingTimes" var="openingClosingTimesLabel"/>
                            <c:forEach var="day" items="${daysOfWeek}">
                                <div class="form-column">
                                    <label>${day}</label>
                                    <select name="${day}OpenTime">
                                        <c:forEach var="time" items="${timeList}">
                                            <option value="${time}">${time}</option>
                                        </c:forEach>
                                    </select>
                                    <select name="${day}CloseTime">
                                        <c:forEach var="time" items="${timeList}">
                                            <option value="${time}">${time}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:forEach>
                        </div>
                    </div>

                    <%--Submit button --%>
                    <div class="d-flex justify-content-end">
                        <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg" style="height:40px;" ><spring:message code="Create.verb"/></button>
                    </div>
                </form:form>
            </div>
        </div>

        <div class="column-info" >
            <div class="cool-static-container" >
                Eventualmente algo tipo puede ser el perfil, o un par de indicaciones de como crear el posteo (reddit tira esa) o el clima o el calendario
            </div>
        </div>
    </div>
</div>

<div id="loader-container" class="loader-container ">
    <div class="cool-static-container medium-size-container" >
        <%@ include file="/WEB-INF/jsp/components/placeholderBlogpost.jsp" %>

        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.your.post"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
