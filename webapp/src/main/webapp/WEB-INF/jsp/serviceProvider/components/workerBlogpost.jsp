<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div id="blogpost-container" class="worker-blogpost">
    <div class="container">
        <div class="f-c-c-c pt-3 pb-3">
            <div class="post-header w-100" >
                <div class="blogpost-author-and-date">
                    <div class="f-r-s-c placeholder-glow" style="gap: 3px">
                        <img
                                id="worker-image-${param.postID}"
                                src=""
                                class="small-profile-picture placeholder"
                                alt="profile_picture_img"
                        />
                        <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>

                        <script>
                            (function () {
                                getImageInto("worker-image-${param.postID}", ${empty param.postUserProfilePictureId ? 0: param.postUserProfilePictureId}, "${pageContext.request.contextPath}");
                            })();
                        </script>
                        <span class="post-author"><c:out value="${param.postNeighborMail}"/></span>

                    </div>

                    <div style="font-size: 12px;color: var(--lighttext);">
                        <spring:message code="posted"/>
                        <span class="post-date" data-post-date="<c:out value="${param.postDate}"/>">
                                </span>
                    </div>

                </div>
                <h1 class="post-title"><c:out value="${param.postTitle}"/></h1>
            </div>

            <div class="w-100">
                <p class="worker-post-description">
                    <c:out value="${param.postDescription}"/>

                </p>
            </div>


            <c:if test="${ param.postImage != 0}">
                <div style="display: flex; justify-content: center; align-items: center;">
                    <img class="blogpost-image"
                         src="${pageContext.request.contextPath}/images/<c:out value="${param.postImage}"/>"
                         alt="post_<c:out value="${param.postID}"/>_img "/>
                </div>
            </c:if>
        </div>

    </div>
</div>


<script src="${pageContext.request.contextPath}/resources/js/dateFormatter.js"></script>

