<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="blogpost" aria-hidden="true">
    <div class="card-body">
        <div class="post-header">
            <h6 class="card-title placeholder-glow">
                <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            </h6>
            <h3 class="card-title placeholder-glow">
                <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            </h3>
        </div>
        <p id="random-placeholders" class="card-text placeholder-glow">
            <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
            <span class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>"></span>
        </p>
    </div>
</div>
