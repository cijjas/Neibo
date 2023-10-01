<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <title><spring:message code="CreateNewEvent.title"/></title>
</head>

<body  class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">
    <div class="row">


        <div class="column-publish" >
            <div class="cool-static-container" >
                <h2 class="card-title"><spring:message code="CreateNewEvent.title"/></h2>
                <div class="divider"></div>
                <!-- Post Creation Form -->
                <form:form method="post" action="addEvent" modelAttribute="eventForm" enctype="multipart/form-data">
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

                        <div class="row">
                            <div class="col-md-6">
                                <form:input path="date" id="date" type="date" required="true" class="cool-input" />
                                <form:errors path="date" cssClass="error" element="p" />
                            </div>

                            <div class="col-md-6">
                                <spring:message code="Duration" var="durationPlaceholder" />
                                <form:input path="duration" class="cool-input" placeholder="${durationPlaceholder}" pattern="[0-9]*" />
                                <div class="form-row form-error">
                                    <form:errors path="duration" cssClass="error" element="p" />
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="divider"></div>

                    <%--Submit button --%>
                    <div class="d-flex justify-content-end">
                        <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg" style="height:40px;" ><spring:message code="Create.verb"/></button>
                    </div>

                </form:form>
            </div>
        </div>

        <c:if test="${showSuccessMessage == true}">
        <c:set var="successMessage">
            <spring:message code="Event.created.successfully"/>
        </c:set>

            <jsp:include page="/WEB-INF/jsp/components/successDialog.jsp" >
                <jsp:param name="successMessage" value="${successMessage}" />
            </jsp:include>
            <script>
                // JavaScript to show the success message with fade-in effect
                const successContainer = document.getElementById('success-container');
                eessContainer.style.display = 'flex'; // Show the container
                setTimeout(function() {
                    successContainer.style.opacity = '1'; // Fade in
                }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition

                // JavaScript to hide the success message with fade-out effect after 2 seconds
                setTimeout(function() {
                    successContainer.style.opacity = '0'; // Fade out
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/redirectToChannel'; // Replace with your desired URL

                    // Optionally, you can add any form data or parameters here
                    // For example, adding a hidden input field with a value
                    const input = document.createElement('input');

                    // Append the input field to the form
                    form.appendChild(input);

                    // Append the form to the document and submit it
                    document.body.appendChild(form);
                    form.submit();

                    setTimeout(function() {
                        successContainer.style.display = 'none'; // Hide the container
                    }, 500); // Wait for the transition to complete (adjust timing as needed)
                }, 2000); // 2000 milliseconds (2 seconds)
            </script>

        </c:if>

    </div>
</div>

<div id="loader-container" class="loader-container ">
    <div class="cool-static-container medium-size-container" >
        <%@ include file="/WEB-INF/jsp/components/placeholderBlogpost.jsp" %>

        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.your.event"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>