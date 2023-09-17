<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="blogpost" aria-hidden="true">
    <div class="card-body">
        <div class="post-header">
            <h6 class="card-title placeholder-glow">
                <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            </h6>
            <h3 class="card-title placeholder-glow">
                <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            </h3>
        </div>
        <p id="random-placeholders" class="card-text placeholder-glow">
            <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
            <span class="placeholder col-<%= Math.floor(Math.random() * 12) + 1 %>"></span>
        </p>
    </div>
</div>


<script>
    // Function to generate a random number between min and max (inclusive)
    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    // Select all elements with the "placeholder" class
    const placeholders = document.querySelectorAll('.placeholder');

    // Loop through each placeholder and set a random col-N class
    placeholders.forEach(function (placeholder) {
        const randomColClass = 'col-' + getRandomInt(1, 12); // You can adjust the range as needed
        placeholder.classList.add(randomColClass);
    });
</script>
