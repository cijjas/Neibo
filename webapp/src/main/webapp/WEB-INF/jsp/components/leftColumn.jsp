<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<div class="left-column-container">


    <div class="left-box" >
        <a href="${pageContext.request.contextPath}/announcements" class="cool-left-button ${channel == 'Announcements' ? 'active' : ''}">
            <i class="fas fa-bullhorn"></i>
            <spring:message code="Announcements"/>
        </a>
        <a href="${pageContext.request.contextPath}/" class="cool-left-button ${channel == 'Feed' ? 'active' : ''}"}>
            <i class="fas fa-comments"></i> <spring:message code="Feed"/>
        </a>
        <a href="${pageContext.request.contextPath}/forum" class="cool-left-button ${channel == 'Forum' ? 'active' : ''}">
            <i class="fas fa-envelope"></i> <spring:message code="Forum"/>
        </a>
    </div>

    <div class="sticky-bottom-button">
        <button id="language-toggle" class="cool-button " >Language</button>
        <button id="dark-mode-toggle" class="cool-button " >Toggle Dark Mode</button>
    </div>


</div>



<script>
    // JavaScript to toggle dark mode
    const darkModeToggle = document.getElementById('dark-mode-toggle');
    const htmlElement = document.documentElement; // Get the HTML element

    // Function to toggle dark mode
    function toggleDarkMode() {
        htmlElement.classList.toggle('dark-mode');

        // Check if dark mode is enabled and update button text
        if (htmlElement.classList.contains('dark-mode')) {
            darkModeToggle.textContent = '<spring:message code="LightMode"/>';
        } else {
            darkModeToggle.textContent = '<spring:message code="DarkMode"/>';
        }
    }

    // Add a click event listener to the toggle button
    darkModeToggle.addEventListener('click', toggleDarkMode);

    // Check the initial dark mode state and set the button text accordingly
    if (htmlElement.classList.contains('dark-mode')) {
        darkModeToggle.textContent = '<spring:message code="LightMode"/>';
    } else {
        darkModeToggle.textContent = '<spring:message code="DarkMode"/>';
    }

    const container = document.querySelector(".left-column-container");
    const stickyItem = document.querySelector(".left-box");


</script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
