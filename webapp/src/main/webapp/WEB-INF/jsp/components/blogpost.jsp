<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/9/2023
  Time: 6:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<div class="post" style="word-wrap: break-word;" aria-hidden="true">
    <a href="/posts/${param.postID}" style="text-decoration: none;">
        <div class="post-header">
            <span class="post-author">${param.postNeighborMail}</span>
            <span class="post-date">publicado el ${param.postDate}</span>
            <h1 class="post-title">${param.postTitle}</h1>
        </div>

    </a>
    <p class="post-description">${param.postDescription}</p>

    <c:if test="${not empty param.postImage}">
        <div style="display: flex; justify-content: center; align-items: center;">
            <img src="/image/${param.postID}" alt="car_image" style="max-width: 100%; max-height: 100vh;"/>
        </div>
    </c:if>

</div>


<%--
<div class="post" aria-hidden="true">
    <div class="card-body">
        <div class="post-header">

            <h6 class="card-title placeholder-glow">
                <span class="placeholder col-6"></span>
            </h6>
            <h3 class="card-title placeholder-glow">
                <span class="placeholder col-7"></span>
            </h3>
        </div>

        <p class="card-text placeholder-glow">
            <span class="placeholder col-7"></span>
            <span class="placeholder col-4"></span>
            <span class="placeholder col-4"></span>
            <span class="placeholder col-7"></span>
            <span class="placeholder col-8"></span>
        </p>
    </div>
</div>
--%>
