<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/10/2023
  Time: 1:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="btn-group-vertical" id="channel-buttons">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        $(document).ready(function () {
            $(".menu-button").click(function () {
                // Remove the 'active' class from all buttons
                $(".menu-button").removeClass("active");

                // Add the 'active' class to the clicked button
                $(this).addClass("active");

                // Get the data-channel attribute value to determine the selected channel
                const selectedChannel = $(this).data("channel");

                // You can perform actions based on the selected channel here
            });
        });
    </script>


    <button type="button" class="btn btn-light mb-2 menu-button active" data-channel="anuncios">
        <i class="fas fa-bullhorn"></i> Anuncios
    </button>
    <button type="button" class="btn btn-light mb-2 menu-button" data-channel="foro">
        <i class="fas fa-comments"></i> Foro
    </button>
    <button type="button" class="btn btn-light mb-2 menu-button" data-channel="contactos">
        <i class="fas fa-address-card"></i> Contactos
    </button>
</div>

