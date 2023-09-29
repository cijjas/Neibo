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
    <title>Neibo - ${post.title}</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/postcard.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<!-- Navigation Bar -->
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

    <div class="container ">
        <div class="row">
            <div class="column column-left">
                <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
            </div>

            <div class="column column-middle ">
                <%@ include file="/WEB-INF/jsp/components/postcard.jsp" %>
            </div>

            <div class="column column-right">
                <%@ include file="/WEB-INF/jsp/components/rightColumn.jsp" %>
            </div>
        </div>
    </div>

    <c:if test="${param.success == 'true'}">
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
            <div style="text-align:center;font-weight: bold; font-size: 24px; color:var(--old-primary)"><spring:message code="Post.created.successfully"/></div>
        </div>
    </div>
        <script>
            // JavaScript to show the success message with fade-in effect
            const successContainer = document.getElementById('success-container');
            successContainer.style.display = 'flex'; // Show the container
            setTimeout(function() {
                successContainer.style.opacity = '1'; // Fade in
            }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition

            // JavaScript to hide the success message with fade-out effect after 2 seconds
            setTimeout(function() {
                successContainer.style.opacity = '0'; // Fade out
                setTimeout(function() {
                    successContainer.style.display = 'none'; // Hide the container
                }, 500); // Wait for the transition to complete (adjust timing as needed)
            }, 2000); // 2000 milliseconds (2 seconds)
        </script>
    </c:if>




    <!-- Bootstrap JS and jQuery -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
