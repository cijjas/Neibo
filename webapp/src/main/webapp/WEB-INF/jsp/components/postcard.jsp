<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container">
    <!-- Post information -->
    <div class="post-section">
        <div class="post-info">
            <h2><c:out value="${post.title}" /></h2>
            <p><spring:message code="PostedBy"/> <c:out value="${post.user.name}" /></p>
            <div class="postcard-description">
                <c:out value="${post.description}" />
            </div>
        </div >

    <!-- Image section -->
    <c:if test="${not empty post.imageFile}">
        <div style="display: flex; justify-content: center; align-items: center;">
            <img src="${pageContext.request.contextPath}/postImage/<c:out value="${post.postId}"/>"
                 style="max-width: 100%; max-height: 100vh; border-radius: 5px;"
                 alt="post_${post.postId}_img"/>
        </div>
    </c:if>

    </div>

    <!-- Tag section -->
    <div class="tag-section">
        <div class="tag-list">
            <c:choose>
                <c:when test="${empty tags}">
                </c:when>
                <c:otherwise>
                    <p><strong> <spring:message code="Tags"/></strong> </p>
                    <c:forEach var="tag" items="${tags}">
                        <a href="${pageContext.request.contextPath}/?page=1&sortBy=tag${tag.tag}">
                            <span class="badge badge-primary" >#<c:out value="${tag.tag}" /></span>
                        </a>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Comment section -->
    <div class="comments-section">
        <!-- Input box for adding a new comment -->
        <div class="comments-section-divider">
            <h5 class="divider-title"><spring:message code="CommentSection"/></h5>
            <hr class="divider-line">
        </div>

        <div style="margin: 10px;">

            <form:form method="post" action="${post.postId}" modelAttribute="commentForm">
                <form:errors cssClass="error" element="p"/>

                <!-- Message input -->
                <div class="form-group">
                    <spring:message code="Comment" var="commentPlaceholder"/>
                    <form:textarea path="comment" class="cool-input" rows="3" placeholder="${commentPlaceholder}"/>
                    <div class="form-row form-error">
                        <form:errors path="comment" cssClass="error" element="p"/>
                    </div>
                </div>

                <!-- Submit button -->
                <div class="d-flex justify-content-end">
                    <button type="submit" class="cool-button cool-small on-bg">
                        <spring:message code="Comment.verb"/>
                    </button>
                </div>

            </form:form>
        </div>

        <c:choose>
            <c:when test="${empty comments}">
                <div class="cool-comment">
                    <div class="comment-body">
                        <spring:message code="NoComments"/>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="comment" items="${comments}">
                    <div class="cool-comment">
                        <div class="comment-header">
                            <strong><c:out value="${comment.user.name} ${comment.user.surname}" /></strong>
                        </div>
                        <div class="comment-body">
                            <c:out value="${comment.comment}" />
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>


</div>