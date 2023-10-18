<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div id="success-container" class="success-container">
    <div class="cool-static-container small-size-container justify-content-around ">
        <div>
            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 50 50" fill="none">
                <g clip-path="url(#clip0_259_2)">
                    <path d="M25 50C38.8071 50 50 38.8071 50 25C50 11.1929 38.8071 0 25 0C11.1929 0 0 11.1929 0 25C0 38.8071 11.1929 50 25 50Z"
                          fill="var(--old-primary)"></path>
                    <path d="M38 15L22 33L12 25" stroke="var(--background)" stroke-width="2" stroke-miterlimit="10"
                          stroke-linecap="round" stroke-linejoin="round"></path>
                </g>
                <defs>
                    <clipPath id="clip0_259_2">
                        <rect width="50" height="50" fill="var(--background)"></rect>
                    </clipPath>
                </defs>
            </svg>
        </div>

        <div style="text-align:center;font-weight: bold; font-size: 24px; color:var(--old-primary)"><c:out
                value="${param.successMessage}"/></div>
    </div>
</div>
<script>
    // JavaScript to show the success message with fade-in effect
    const successContainer = document.getElementById('success-container');
    successContainer.style.display = 'flex'; // Show the container
    setTimeout(function () {
        successContainer.style.opacity = '1'; // Fade in
    }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition

    // JavaScript to hide the success message with fade-out effect after 2 seconds
    setTimeout(function () {
        successContainer.style.opacity = '0'; // Fade out
        setTimeout(function () {
            successContainer.style.display = 'none'; // Hide the container
        }, 500); // Wait for the transition to complete (adjust timing as needed)
    }, 2000); // 2000 milliseconds (2 seconds)
</script>
