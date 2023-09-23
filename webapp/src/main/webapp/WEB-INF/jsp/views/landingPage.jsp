<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <title>Welcome to Neibo</title>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/landingPage.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>
<body class="landing-body">
<%----%>

    <%@ include file="/WEB-INF/jsp/components/backgroundDrawing.jsp" %>


    <div class="content-container">
        <%@ include file="/WEB-INF/jsp/components/landingNavbar.jsp" %>
        <div class="landing-neibo">
            neibo
        </div>
        <p class="landing-description">Lorem ipsum en el culo te pellizco aguante boca la puta madre Lorem Ipsum is simply dummy text of the printing and typesetting industry.</p>

        <a class="action-button"  onclick="openLoginDialog()">
            Log In
        </a>
        <span style="color:var(--lighttext); font-size: 14px;">Not a member?
            <a onclick="openSignupDialog()" class="a-link">Signup now</a>
        </span>

        <div class="dialog" id="loginDialog" style="display: none">
            <div class="dialog-content">
                <div class="close-button" onclick="closeLoginDialog()">
                    <i class="fas fa-close"></i>
                </div>
                <div class="title">
                    Welcome to neibo
                    <br>
                    <span>Log in to continue</span>
                </div>

                <form method="post" class="login-form">
                    <div class="centered-column">
                        <label>
                            <input type="email" placeholder="Email" name="mail" class="input">
                        </label>
                        <label>
                            <input type="password" placeholder="Password" name="password" class="input">
                        </label>

                    </div>
                    <label class="centered-row light-text">
                        <input  name="rememberMe" type="checkbox">
                        Remember Me!
                    </label>
                    <div class="centered-column">
                        <button class="action-button">Login</button>
                        <span style="color:var(--lighttext); font-size: 14px;">Not a member?
                            <a onclick="closeLoginDialog();openSignupDialog()" class="a-link">Signup now</a>
                        </span>
                    </div>

                </form>
            </div>
        </div>


        <div class="dialog" id="signupDialog" style="display: none">
            <div class="dialog-content">
                <div class="close-button" onclick="closeSignupDialog()">
                    <i class="fas fa-close"></i>
                </div>
                <div class="title">
                    Welcome to neibo
                    <br>
                    <span>Sign up to get started</span>
                </div>

                <form:form method="post" action="signup" modelAttribute="signupForm">
                    <form:errors cssClass="error" element="p"/>
                    <div class="form-input">
                        <form:label path="name">
                            <form:input path="name" placeholder="First Name" class="input"/>
                        </form:label>
                        <form:errors path="name" cssClass="error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="surname">
                            <form:input path="surname" placeholder="Surname" class="input"/>
                        </form:label>
                        <form:errors path="surname" cssClass="error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="mail">
                            <form:input path="mail" placeholder="Email" class="input"/>
                        </form:label>
                        <form:errors path="mail" cssClass="error" element="p"/>
                    </div>
                    <div class="form-input">
                        <form:label path="password">
                            <form:input path="password" placeholder="Password" class="input"/>
                        </form:label>
                        <form:errors path="password" cssClass="error" element="p"/>
                    </div>

                    <div class="form-input">
                        <form:label path="neighborhoodId"> Neighborhoods </form:label>
                        <form:select path="neighborhoodId" class="form-control">
                                <c:forEach var="entry" items="${neighborhoodsList}">
                                    <form:option value="${entry.getNeighborhoodId()}">${entry.getName()}</form:option>
                                </c:forEach>
                        </form:select>
                    </div>

                    <button type="submit" class="action-button">Sign Up</button>
                </form:form>
            </div>
        </div>

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
         }
     </script>

<%--  para que se abra el dialogo de signup al cargar la pagina (no funca)--%>
<%--    <script>--%>
<%--        window.onload = function () {--%>
<%--            var openSignupDialog = ${openSignupDialog}; // Get the variable value--%>

<%--            if (openSignupDialog) {--%>
<%--                openSignupDialog(); // Open the signup dialog if the variable is true--%>
<%--            }--%>
<%--        }--%>
<%--    </script>--%>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
