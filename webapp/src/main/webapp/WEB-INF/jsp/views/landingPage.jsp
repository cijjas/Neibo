
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

        <a class="action-button"  onclick="openForm()">
            Log In
        </a>
        <span style="color:var(--lighttext); font-size: 14px;">Not a member?
            <a onclick="openRegister()" class="a-link">Signup now</a>
        </span>


        <div class="fixed-middle" id="myForm" style="display: none">

            <form class="login-form" >
                <a class="close-button" onclick="closeForm()">
                    <i class="fas fa-close"></i>
                </a>
                <div class="title">Welcome to neibo
                    <br>
                    <span>Log in to continue</span>
                </div>
                <label>
                    <input type="email" placeholder="Email" name="email" class="input">
                </label>
                <label>
                    <input type="password" placeholder="Password" name="password" class="input">
                </label>

                <button class="action-button">Login</button>
            </form>
        </div>

    </div>

     <script>
         function openForm() {
             document.getElementById("myForm").style.display = "block";
         }

         function closeForm() {
             document.getElementById("myForm").style.display = "none";
         }
     </script>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
