<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/9/2023
  Time: 6:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="post" style="word-wrap: break-word;">
    <div class="post-header">
        <span class="post-author">${param.postNeighborMail}</span>
        <span class="post-date">publicado el ${param.postDate}</span>
        <h1 class="post-title">${param.postTitle}</h1>
    </div>
        <p class="post-description">${param.postDescription}</p>
    <div class="card-footer">
        <small class="text-muted">Por: ${param.postNeighborMail}</small>
    </div>
</div>

