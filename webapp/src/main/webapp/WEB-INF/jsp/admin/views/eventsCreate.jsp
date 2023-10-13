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
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row">


        <div class="column-publish" >
            <div class="cool-static-container" >
                <h2 class="card-title"><spring:message code="CreateNewEvent.title"/></h2>
                <div class="divider"></div>
                <!-- Post Creation Form -->
                <form:form method="post" action="add-event" modelAttribute="eventForm" enctype="multipart/form-data">
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
                                <form:input path="date" id="date" type="date" required="true" class="cool-input"/>
                                <form:errors path="date" cssClass="error" element="p" />
                            </div>

                            <div class="col-md-3">
                                <spring:message code="Hours" var="hoursPlaceholder" />
                                <form:input path="startTimeHours" class="cool-input" placeholder="${hoursPlaceholder}" type="number" min="0" max="23" />
                                <div class="form-row form-error">
                                    <form:errors path="startTimeHours" cssClass="error" element="p" />
                                </div>
                            </div>

                            <div class="col-md-3">
                                <spring:message code="Minutes" var="minutesPlaceholder" />
                                <form:input path="startTimeMinutes" class="cool-input" placeholder="${minutesPlaceholder}" type="number" min="0" max="59" />
                                <div class="form-row form-error">
                                    <form:errors path="startTimeMinutes" cssClass="error" element="p" />
                                </div>
                            </div>

                            <div class="col-md-3">
                                <spring:message code="Hours" var="hoursPlaceholder" />
                                <form:input path="endTimeHours" class="cool-input" placeholder="${hoursPlaceholder}" type="number" min="0" max="23" />
                                <div class="form-row form-error">
                                    <form:errors path="startTimeHours" cssClass="error" element="p" />
                                </div>
                            </div>

                            <div class="col-md-3">
                                <spring:message code="Minutes" var="minutesPlaceholder" />
                                <form:input path="endTimeMinutes" class="cool-input" placeholder="${minutesPlaceholder}" type="number" min="0" max="59" />
                                <div class="form-row form-error">
                                    <form:errors path="startTimeMinutes" cssClass="error" element="p" />
                                </div>
                            </div>

                        </div>
                    </div>


                    <%--Submit button --%>
                    <div class="d-flex justify-content-end mt-3">
                        <button type="submit" class="cool-button cool-small on-bg w-25 font-weight-bolder" style="height:40px;" ><spring:message code="Create.verb"/></button>
                    </div>

                </form:form>
            </div>
        </div>

        <c:if test="${showSuccessMessage == true}">

            <div id="success-container" class="success-container">
                <div class="cool-static-container small-size-container justify-content-around ">
                    <div>
                        <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 50 50" fill="none">
                            <g clip-path="url(#clip0_259_2)">
                                <path d="M25 50C38.8071 50 50 38.8071 50 25C50 11.1929 38.8071 0 25 0C11.1929 0 0 11.1929 0 25C0 38.8071 11.1929 50 25 50Z" fill="var(--old-primary)"></path>
                                <path d="M38 15L22 33L12 25" stroke="var(--background)" stroke-width="2" stroke-miterlimit="10" stroke-linecap="round" stroke-linejoin="round"></path>
                            </g>
                            <defs>
                                <clipPath id="clip0_259_2">
                                    <rect width="50" height="50" fill="var(--background)"></rect>
                                </clipPath>
                            </defs>
                        </svg>
                    </div>

                    <div style="text-align:center;font-weight: bold; font-size: 24px; color:var(--old-primary)"> <spring:message code="Event.created.successfully"/> </div>
                </div>
            </div>

            <script>
                // JavaScript to show the success message with fade-in effect
                const successContainer = document.getElementById('success-container');
                successContainer.style.display = 'flex'; // Show the container
                setTimeout(function() {
                    successContainer.style.opacity = '1'; // Fade in
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/redirect-to-site'; // Replace with your desired URL
                    const input = document.createElement('input');
                    input.type='hidden';
                    input.name='site';
                    input.value='calendar';

                    // Append the input field to the form
                    form.appendChild(input);

                    // Append the form to the document and submit it
                    document.body.appendChild(form);
                    form.submit();
                }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition

            </script>

        </c:if>

    </div>
</div>

<div id="loader-container" class="loader-container ">
    <div class="cool-static-container medium-size-container" >
        <%@ include file="/WEB-INF/jsp/components/widgets/placeholderBlogpost.jsp" %>

        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.your.event"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>
</html>