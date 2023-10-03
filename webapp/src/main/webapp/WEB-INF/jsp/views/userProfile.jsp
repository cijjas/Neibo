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
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="Profile"/></title>

</head>
<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%----%>
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container"> <!-- Wrap card content in a container -->
    <div class="cool-static-container p-0">
        <div class="page-content page-container" id="page-content">
            <div class="row m-l-0 m-r-0">
                <div class="col-sm-4 bg-c-lite-green user-profile">
                    <div class="card-block text-center text-white">
                        <div class="m-b-25">
                            <div class="profile-pic">
                                <form:form method="post" action="${pageContext.request.contextPath}/profile" modelAttribute="profilePictureForm" enctype="multipart/form-data" id="profilePicForm">
                                    <form:errors cssClass="error" element="p"/>
                                    <label class="-label" for="file">
                                        <span class="glyphicon glyphicon-camera"></span>
                                        <span><spring:message code="ChangeImage"/></span>
                                    </label>
                                    <form:input path="imageFile" id="file" type="file" accept="image/*" onchange="loadFile()"/>
                                    <c:choose>
                                        <c:when test="${ neighbor.profilePictureId != 0}">
                                            <img id="output" src="${pageContext.request.contextPath}/images/<c:out value="${neighbor.profilePictureId}"/>" alt="profile_picture_img" />
                                        </c:when>
                                        <c:otherwise>
                                            <img id="output" src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />
                                        </c:otherwise>
                                    </c:choose>
                                </form:form>
                            </div>

                            <script>
                                function loadFile() {
                                    const image = document.getElementById("output");
                                    image.src = URL.createObjectURL(event.target.files[0]);
                                    document.getElementById("profilePicForm").submit();
                                }
                            </script>

                        </div>
                        <h6 class="f-w-600"><c:out value="${neighbor.name}"/></h6>
                        <p><c:out value="${neighbor.role}"/></p>


                    </div>
                </div>
                <div class="col-sm-8">
                    <div class="card-block">
                        <h6 class="m-b-20 p-b-5 b-b-default f-w-600"><spring:message code="Information"/></h6>
                        <div class="row">
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600 " ><spring:message code="Email"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.mail}"/></h6>
                            </div>
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600 " ><spring:message code="Name"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.name}" /></h6>
                            </div>
                        </div>
                        <div class="divider m-t-40"></div>
                        <div class="row m-t-40">
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600 " ><spring:message code="Surname"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.surname}"/></h6>
                            </div>
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600 " ><spring:message code="User.since"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.creationDate}" /></h6>
                            </div>
                        </div>
                        <h6 class="m-b-20 m-t-40 p-b-5 b-b-default f-w-600"><spring:message code="Preferences"/></h6>
                        <div class="row m-t-40">
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600 " ><spring:message code="DarkMode"/></p>
                                <div class="controlled" >
                                    <h6 class="text-muted f-w-400"><spring:message code="Off"/></h6>
                                    <form id="dark-mode-form" action="${pageContext.request.contextPath}/updateDarkModePreference" method="POST">
                                        <label class="switch">
                                            <input class="toggle" type="checkbox" id="dark-mode-toggle" name="darkMode">
                                            <span class="slider"></span>
                                            <span class="card-side"></span>
                                        </label>
                                    </form>
                                    <h6 class="text-muted f-w-400"><spring:message code="On"/></h6>
                                </div>
                            </div>

                        </div>


                        <div class="row justify-content-end">
                            <a href="${pageContext.request.contextPath}/logout" class=" cool-button cool-small on-bg">
                                    <spring:message code="Logout"/>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<input id="dark-mode-toggle-var" type="hidden" value="${neighbor.darkMode}"/>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        document.getElementById("dark-mode-toggle").checked = document.getElementById("dark-mode-toggle-var").value === "true";

        const darkModeToggle = document.getElementById("dark-mode-toggle");
        const darkModeForm = document.getElementById("dark-mode-form");

        // Add an event listener to the checkbox
        darkModeToggle.addEventListener("change", function() {

            // Automatically submit the form when the checkbox changes
            darkModeForm.submit();
        });

        const languageToggle = document.getElementById("language-toggle");

        languageToggle.addEventListener("change", function() {

        });
    });



</script>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>


<%----%>
</body>
</html>
