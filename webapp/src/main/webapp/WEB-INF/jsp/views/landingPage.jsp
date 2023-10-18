<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/gh/habibmhamadi/multi-select-tag/dist/css/multi-select-tag.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <title><spring:message code="Welcome.to.neibo"/></title>
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>

    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/landingPage.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>
<body class="landing-body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%----%>


<c:if test="${error == true}">
    <c:set var="errorMessage">
        <spring:message code="Login.error.message.1"/>
        <spring:message code="Login.error.message.2"/>
    </c:set>

    <jsp:include page="/WEB-INF/jsp/errors/errorDialog.jsp">
        <jsp:param name="errorMessage" value="${errorMessage}"/>
        <jsp:param name="openLoginAgain" value="${true}"/>
    </jsp:include>
</c:if>

<c:if test="${successfullySignup == true}">
    <c:set var="successMessage">
        <spring:message code="Successfully.signup"/>
    </c:set>

    <jsp:include page="/WEB-INF/jsp/components/widgets/successDialog.jsp">
        <jsp:param name="successMessage" value="${successMessage}"/>
    </jsp:include>
    <script>
        setTimeout(function () {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/redirect-to-channel';

            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'channelId';
            input.value = 'login';

            form.appendChild(input);

            document.body.appendChild(form);
            form.submit();
        }, 2500);
    </script>
</c:if>

<div class="content-container ">
    <div class="container f-c-c-c">
        <%@ include file="/WEB-INF/jsp/components/displays/landingNavbar.jsp" %>
        <div class="landing-neibo ">
            neibo
        </div>
        <p class="landing-description" style="mix-blend-mode: screen"><spring:message code="Landing.page.desc"/></p>

        <a class="action-button font-weight-bolder" onclick="openLoginDialog()">
            <spring:message code="Login"/>
        </a>
        <span style="color:var(--lighttext); font-size: 14px; mix-blend-mode: screen">
            <spring:message code="Not.a.member.question"/>
            <a onclick="openSignupDialog()" class="a-link">
                <spring:message code="Signup.now"/>
            </a>
        </span>


        <%-- Login dialog--%>
        <%@ include file="/WEB-INF/jsp/components/widgets/landingPage/loginDialog.jsp" %>

        <%-- Signup dialog--%>
        <%@ include file="/WEB-INF/jsp/components/widgets/landingPage/signupDialog.jsp" %>

        <%-- Worker signup dialog--%>
        <%@ include file="/WEB-INF/jsp/components/widgets/landingPage/workerSignupDialog.jsp" %>

        <%--Signup dialog--%>
        <c:if test="${openSignupDialog == true}">
            <script>
                document.getElementById("signupDialog").style.display = "flex";
            </script>
        </c:if>

        <c:if test="${openWorkerSignupDialog == true}">
            <script>
                document.getElementById("workerSignupDialog").style.display = "flex";
            </script>
        </c:if>
    </div>

    <%@ include file="/WEB-INF/jsp/components/structure/backgroundDrawing.jsp" %>

</div>

<%-- <div class="container f-c-c-c">--%>
<%-- </div>--%>


<script>
    function submitSignupForm() {
        document.forms["signupForm"].submit();
    }

    function openLoginDialog() {
        document.getElementById("loginDialog").style.display = "flex";
    }

    function closeLoginDialog() {
        document.getElementById("loginDialog").style.display = "none";
    }

    function openSignupDialog() {
        document.getElementById("signupDialog").style.display = "flex";
    }

    function closeSignupDialog() {
        document.getElementById("signupDialog").style.display = "none";

        clearFormErrors();
    }

    function openWorkerSignupDialog() {
        document.getElementById("workerSignupDialog").style.display = "flex";
    }

    function closeWorkerSignupDialog() {
        document.getElementById("workerSignupDialog").style.display = "none";
        clearFormErrors();


    }

    function takeToLogin() {
        window.location.href = "/login";
    }

    function clearFormErrors() {
        const formElements = document.querySelectorAll("#signupForm input");
        formElements.forEach(function (element) {
            element.value = "";
        });

        // Clear error messages
        const errorElements = document.querySelectorAll(".landing-error");
        errorElements.forEach(function (element) {
            element.textContent = ""; // Clear the error message text
        });
    }
</script>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
</body>
</html>
