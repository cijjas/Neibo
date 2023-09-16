<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/11/2023
  Time: 11:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container">
    <!-- Post information -->
    <div class="post-section">
        <div class="post-info">
            <h2><c:out value="${post.title}" /></h2>
            <p><spring:message code="PostedBy"/> <c:out value="${post.neighbor.name}" /></p>
            <p><c:out value="${post.description}" /></p>
        </div >

    <!-- Image section -->
    <c:if test="${not empty post.imageFile}">
        <div style="display: flex; justify-content: center; align-items: center;">
            <img src="/postImage/${post.postId}" style="max-width: 100%; max-height: 100vh;"/>
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
                        <span class="badge badge-primary">#<c:out value="${tag.tag}" /></span>
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

        <form:form method="post" action="/posts/${post.postId}" modelAttribute="commentForm">
            <form:errors cssClass="error" element="p"/>
            <!-- Name input -->
            <div class="form-row">
                <!-- First row -->
                <div class="form-outline col-md-6 mb-4">
                    <form:input path="name" class="form-control" />
                    <form:label class="form-label" path="name"><spring:message code="Name"/></form:label>
                    <form:errors path="name" cssClass="error" element="p"/>
                </div>
                <div class="form-outline col-md-6 mb-4">
                    <form:input path="surname" class="form-control" />
                    <form:label class="form-label" path="surname"><spring:message code="Surname"/></form:label>
                    <form:errors path="surname" cssClass="error" element="p"/>
                </div>
            </div>
            <div class="form-row">
                <!-- Second row -->
                <div class="form-outline col-md-6 mb-4">
                    <form:input path="email" class="form-control" />
                    <form:label class="form-label" path="email"><spring:message code="Email"/></form:label>
                    <form:errors path="email" cssClass="error" element="p"/>
                </div>
                <div class="form-outline col-md-6 mb-4">
                    <form:input path="neighborhood" class="form-control" />
                    <form:label class="form-label" path="neighborhood"><spring:message code="Neighborhood"/></form:label>
                    <form:errors path="neighborhood" cssClass="error" element="p"/>
                </div>
            </div>

            <!-- Message input -->
            <div class="form-outline mb-4">
                <form:textarea path="comment" class="form-control" />
                <form:label class="form-label" path="comment"><spring:message code="Comment"/></form:label>
                <form:errors path="comment" cssClass="error" element="p"/>
            </div>

            <!-- Submit button -->
            <button type="submit" class="filter-button">
                <spring:message code="Comment.verb"/>
            </button>
        </form:form>

        <c:choose>
            <c:when test="${empty comments}">
                <div class="comment">
                    <div class="comment-body">
                        <spring:message code="NoComments"/>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="comment" items="${comments}">
                    <div class="comment">
                        <div class="comment-header">
                            <strong><spring:message code="User"/> #<c:out value="${comment.neighborId}" /></strong>
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