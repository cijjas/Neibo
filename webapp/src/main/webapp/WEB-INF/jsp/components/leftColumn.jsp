<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/10/2023
  Time: 1:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="left-column-container">

    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function () {
            $(".left-column-button").click(function () {
                // Check if the clicked button already has the 'active' class
                if (!$(this).hasClass("active")) {
                    // Remove the 'active' class from all buttons
                    $(".left-column-button").removeClass("active");

                    // Add the 'active' class to the clicked button
                    $(this).addClass("active");

                    // Get the data-channel attribute value to determine the selected channel
                    const selectedChannel = $(this).data("channel");

                    // You can perform actions based on the selected channel here
                }
            });
        });
    </script>

    <div class="left-box btn-group-vertical">
        <a href="${pageContext.request.contextPath}/announcements" class="left-column-button">
            <i class="fas fa-bullhorn"></i> <spring:message code="Announcements"/>
        </a>
        <a href="${pageContext.request.contextPath}/" class="left-column-button">
            <i class="fas fa-comments"></i> <spring:message code="Feed"/>
        </a>
        <a href="${pageContext.request.contextPath}/forum" class="left-column-button">
            <i class="fas fa-envelope"></i> <spring:message code="Forum"/>
        </a>
    </div>
    <%-- Dark mode toggle button
    <button id="dark-mode-toggle" class="left-column-button">Toggle Dark Mode</button>

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
    </script>
    --%>


</div>
