<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="success-container" class="success-container">
    <div class="cool-static-container small-size-container justify-content-around ">
        <div>
            <svg width="100" height="100" viewBox="0 0 577 577" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="288.5" cy="288.5" r="288.5" fill="var(--error)"></circle>
                <rect x="261" y="111" width="55" height="245" rx="26" fill="var(--background)"></rect>
                <circle cx="289" cy="416" r="31" fill="#D9D9D9"></circle>
            </svg>
        </div>

        <div style="text-align:center;font-weight: bold; font-size: 24px; color:var(--error)">
            <c:out value="${param.errorMessage}"/>
        </div>
        <a class="cool-button small-a on-bg" href="${pageContext.request.contextPath}/login">
            OK
        </a>
    </div>
</div>
<script>
    // JavaScript to show the success message with fade-in effect
    const successContainer = document.getElementById('success-container');
    successContainer.style.display = 'flex'; // Show the container
    setTimeout(function() {
        successContainer.style.opacity = '1'; // Fade in
    }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition


</script>
