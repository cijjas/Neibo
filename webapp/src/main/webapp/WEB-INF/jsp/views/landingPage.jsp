<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
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

<%@ include file="/WEB-INF/jsp/components/backgroundDrawing.jsp" %>
<%--Successfull signup popup--%>

<%-- show error if parameter error is in url --%>


<c:if test="${error == true}">
    <c:set var="errorMessage">
        <spring:message code="Login.error.message"/>
    </c:set>

    <jsp:include page="/WEB-INF/jsp/errors/errorDialog.jsp" >
        <jsp:param name="errorMessage" value="${errorMessage}" />
    </jsp:include>
</c:if>

<c:if test="${successfullySignup == true}">
    <c:set var="successMessage">
        <spring:message code="Successfully.signup"/>
    </c:set>

    <jsp:include page="/WEB-INF/jsp/components/successDialog.jsp" >
        <jsp:param name="successMessage" value="${successMessage}" />
    </jsp:include>
    <script>
        setTimeout(function() {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/redirectToChannel'; // Replace with your desired URL

            // Optionally, you can add any form data or parameters here
            // For example, adding a hidden input field with a value
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'channelId';
            input.value = 'login';

            // Append the input field to the form
            form.appendChild(input);

            // Append the form to the document and submit it
            document.body.appendChild(form);
            form.submit();
        }, 2500);
    </script>
</c:if>

<div class="content-container">
    <%@ include file="/WEB-INF/jsp/components/landingNavbar.jsp" %>
    <div class="landing-neibo">
        neibo
    </div>
    <p class="landing-description"><spring:message code="Landing.page.desc"/></p>

    <a class="action-button"  onclick="openLoginDialog()">
        <spring:message code="Login"/>
    </a>
    <span style="color:var(--lighttext); font-size: 14px;"><spring:message code="Not.a.member.question"/>
            <a onclick="openSignupDialog()" class="a-link"><spring:message code="Signup.now"/></a>
    </span>

    <%--        Login dialog--%>
    <div class="dialog" id="loginDialog" style="display: none">
        <div class="dialog-content">
            <div class="close-button" onclick="closeLoginDialog()">
                <i class="fas fa-close"></i>
            </div>
            <div class="title">
                <spring:message code="Welcome.to.neibo"/>
                <br>
                <span><spring:message code="Login.to.continue"/> </span>
            </div>

            <form method="post" class="login-form" id="loginForm">
                <div class="centered-column">
                    <label>
                        <c:set var="email"><spring:message code="Email"/></c:set>
                        <input type="email" placeholder="${email}" name="mail" class="input">
                    </label>

                    <label>
                        <c:set var="password"><spring:message code="Password"/></c:set>
                        <input type="password" placeholder="${password}"  autocomplete="true" name="password" class="input">
                    </label>

                </div>
                <label class="centered-row light-text">
                    <input  name="rememberMe" type="checkbox">
                    <spring:message code="Remember.me"/>
                </label>
                <div class="centered-column">

                    <button class="action-button"><spring:message code="Login"/></button>
                    <span style="color:var(--lighttext); font-size: 14px;"><spring:message code="Not.a.member.question"/>
                            <a onclick="closeLoginDialog();openSignupDialog()" class="a-link"><spring:message code="Signup.now"/></a>
                        </span>
                </div>

            </form>
        </div>
    </div>

    <div class="dialog" id="signupDialog" style="display: none">
        <div class="dialog-content signup-content">
            <div class="close-button" onclick="closeSignupDialog()">
                <i class="fas fa-close"></i>
            </div>
            <div class="title">
                <spring:message code="Welcome.to.neibo"/>
                <br>
                <span><spring:message code="Signup.to.get.started"/></span>
            </div>

            <form:form method="post" action="signup" modelAttribute="signupForm" id="signupForm">
                <form:errors cssClass="error" element="p"/>
                <div class="centered-column">

                    <div class="form-input">
                        <form:label path="name">
                            <c:set var="firstName"><spring:message code="First.name"/></c:set>
                            <form:input path="name" placeholder="${firstName}" class="input"/>
                        </form:label>
                        <form:errors path="name" cssClass="landing-error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="surname">
                            <c:set var="surname"><spring:message code="Surname"/></c:set>
                            <form:input path="surname" placeholder="${surname}" class="input"/>
                        </form:label>
                        <form:errors path="surname" cssClass="landing-error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="mail">
                            <c:set var="email"><spring:message code="Email"/></c:set>
                            <form:input path="mail" placeholder="${email}" class="input"/>
                        </form:label>
                        <form:errors path="mail" cssClass="landing-error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="password">
                            <c:set var="password"><spring:message code="Password"/></c:set>
                            <form:input type="password" path="password" autocomplete="true" placeholder="Password" class="input"/>
                        </form:label>
                        <form:errors path="password" cssClass="landing-error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="identification">
                            <c:set var="identification"><spring:message code="Identification"/></c:set>
                            <form:input type="number" pattern="[0-9]*" inputmode="numeric" min="1" max="99999999" path="identification" placeholder="${identification}" class="input"/>
                        </form:label>
                        <form:errors path="identification" cssClass="landing-error" element="p"/>
                    </div>

                    <div class="form-input">
                        <form:label path="language">
                            <form:select path="language" class="cool-select" style="width: 200px;">
                                <form:option value="English"><spring:message code="English"/></form:option>
                                <form:option value="Spanish"><spring:message code="Spanish"/></form:option>
                            </form:select>
                        </form:label>
                        <form:errors path="language" cssClass="landing-error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:select path="neighborhoodId" class="cool-select">
                            <c:forEach var="entry" items="${neighborhoodsList}">
                                <form:option value="${entry.getNeighborhoodId()}"><c:out value="${entry.getName()}"/></form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>

                <button type="submit" class="action-button"><spring:message code="Signup"/></button>
            </form:form>

        </div>
    </div>

    <%--Signup dialog--%>
    <c:if test="${openSignupDialog == true}">
        <script>
            document.getElementById("signupDialog").style.display = "flex";
        </script>
    </c:if>


</div>


<script>
    function openLoginDialog() {
        document.getElementById("loginDialog").style.display = "flex";
    }

    function closeLoginDialog() {
        document.getElementById("loginDialog").style.display = "none";
    }

    function openSignupDialog(){
        document.getElementById("signupDialog").style.display = "flex";
    }
    function closeSignupDialog(){
        document.getElementById("signupDialog").style.display = "none";

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
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
