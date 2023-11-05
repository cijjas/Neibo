<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="errorDialog" class="success-container">
    <div class="cool-static-container red small-size-container f-c-c-c g-2 ">
        <div>
            <svg width="50" height="50" viewBox="0 0 577 577" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="288.5" cy="288.5" r="288.5" fill="var(--error)"></circle>
                <rect x="261" y="111" width="55" height="245" rx="26" fill="var(--background)"></rect>
                <circle cx="289" cy="416" r="31" fill="#D9D9D9"></circle>
            </svg>
        </div>

        <div style="text-align:center;font-weight: normal; font-size: 20px; color:var(--error)">
            <c:out value="${param.errorMessage}"/>
        </div>
        <a class="cool-button red on-bg w-25 font-weight-bolder" onclick="closeErrorDialog()">
            <spring:message code="OK"/>
        </a>
    </div>
</div>
<script>
    function closeErrorDialog() {
        const errorContainer = document.getElementById('errorDialog');
        if ("${param.openLoginAgain}" === "true") {
            openLoginDialog();
        }
        errorContainer.style.display = 'none'; // Hide the container
        errorContainer.style.opacity = '0'; // Fade in
    }


</script>
