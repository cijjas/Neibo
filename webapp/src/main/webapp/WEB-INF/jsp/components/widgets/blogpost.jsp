<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div id="blogpost-container" class="blogpost" >
    <div class="container">
        <div class=" row ">
            <div class="col-md-1 grey-bg col-md-pull-1" >
                <div class="f-c-c-c mt-3">
                    <i class="fa-solid fa-thumbs-up"></i>
                    <i class="fa-regular fa-thumbs-up"></i>
                </div>
            </div>
            <div class="col-md-11 pt-3 pb-3 pr-3 col-md-push-11">
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
                    <c:if test="${ param.postImage != 0}">
                        <div style="display: flex; justify-content: center; align-items: center;">
                            <img class="blogpost-image" src="${pageContext.request.contextPath}/images/<c:out value="${param.postImage}"/>" alt="post_<c:out value="${param.postID}"/>_img " />
                        </div>
                    </c:if>

                </a>
                <div class="mt-2 d-flex flex-row justify-content-start align-items-center flex-wrap">
                    <c:forEach var="postTag" items="${requestScope.postTags}">
                        <a href="${pageContext.request.contextPath}/?tag=${postTag.tag}" class="post-tag static m-l-3 m-r-3" data-post-tag="${postTag.tag}">
                            <c:out value="${postTag.tag}"/>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    async function getPostComment() {
        try {
            const response = await fetch("/api/commentById?id=" + commentId);
            if (!response.ok) {
                throw new Error("Failed to fetch data from the API.");
            }
            const commentElement = document.getElementById("comment-" + commentId);
            commentElement.textContent = await response.text();
            commentElement.classList.remove('placeholder');
        } catch (error) {
            console.error(error.message);
        }
    }
    getPostComment(${comment.commentId});
</script>

<script src="${pageContext.request.contextPath}/resources/js/blogpost.js"></script>

