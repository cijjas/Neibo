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
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container"> <!-- Wrap card content in a container -->
    <div class="cool-static-container p-0">
        <div class="page-content page-container" id="page-content">
            <div class="row m-l-0 m-r-0">
                <div class="col-sm-4 bg-c-lite-green user-profile">
                    <div class="card-block text-center text-white">
                        <div class="m-b-25">
                            <img src="https://img.icons8.com/bubbles/100/000000/user.png" class="img-radius" alt="User-Profile-Image">
                        </div>
                        <h6 class="f-w-600"><c:out value="${neighbor.name}"/></h6>
                        <p><c:out value="${neighbor.role}"/></p>
                        <i class=" mdi mdi-square-edit-outline feather icon-edit m-t-10 f-16"></i>
                    </div>
                </div>
                <div class="col-sm-8">
                    <div class="card-block">
                        <h6 class="m-b-20 p-b-5 b-b-default f-w-600"><spring:message code="Information"/></h6>
                        <div class="row">
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600"><spring:message code="Email"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.mail}"/></h6>
                            </div>
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600"><spring:message code="User"/></p>
                                <h6 class="text-muted f-w-400"><c:out value="${neighbor.userId}" /></h6>
                            </div>
                        </div>
                        <h6 class="m-b-20 m-t-40 p-b-5 b-b-default f-w-600"><spring:message code="Preferences"/></h6>
                        <div class="row">
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600"><spring:message code="DarkMode"/></p>
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
                            <div class="col-sm-6">
                                <p class="m-b-10 f-w-600"><spring:message code="Language"/></p>
                                <div class="controlled" >
                                    <h6 class="text-muted f-w-400"><spring:message code="English"/></h6>
                                    <div >
                                        <label class="switch ">
                                            <input class="toggle" type="checkbox" id="language-toggle">
                                            <span class="slider"></span>
                                            <span class="card-side"></span>
                                        </label>
                                    </div>
                                        <h6 class="text-muted f-w-400"><spring:message code="Spanish"/></h6>
                                </div>
                            </div>
                        </div>
                        <h6 class="m-b-20 m-t-40 p-b-5 b-b-default f-w-600"><spring:message code="Preferences"/></h6>
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
        const darkModeToggle = document.getElementById("dark-mode-toggle");
        const darkModeForm = document.getElementById("dark-mode-form");

        // Add an event listener to the checkbox
        darkModeToggle.addEventListener("change", function() {

            // Automatically submit the form when the checkbox changes
            darkModeForm.submit();
        });
    });
    document.getElementById("dark-mode-toggle").checked = document.getElementById("dark-mode-toggle-var").value === "true";



</script>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>


<%----%>
</body>
</html>
