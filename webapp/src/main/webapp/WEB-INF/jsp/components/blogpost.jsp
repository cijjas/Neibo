<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div id="blogpost-container" class="blogpost" style="word-wrap: break-word;" aria-hidden="true">
    <a href="${pageContext.request.contextPath}/posts/<c:out value="${param.postID}" />" style="text-decoration: none;">
        <div class="post-header">
            <div class="blogpost-author-and-date">
                <span class="post-author"><c:out value="${param.postNeighborMail}" /></span>

                <div style="font-size: 12px;color: var(--lighttext);">
                    <spring:message code="posted"/>
                    <span class="post-date" data-post-date="<c:out value="${param.postDate}"/>">
                    </span>
                </div>

            </div>


            <h1 class="post-title"><c:out value="${param.postTitle}" /></h1>
        </div>
        <p class="post-description"><c:out value="${param.postDescription}" /></p>
        <c:if test="${not empty param.postImage}">
            <div style="display: flex; justify-content: center; align-items: center;">
                <img class="blogpost-image" src="${pageContext.request.contextPath}/postImage/<c:out value="${param.postID}"/>" alt="post_<c:out value="${param.postID}"/>_img " />
            </div>
        </c:if>
    </a>
</div>

<script src="${pageContext.request.contextPath}/resources/js/blogpost.js"></script>

