<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div id="blogpost-container" class="blogpost">
    <div class="container">
        <div class=" row ">
            <div class="col-md-1 grey-bg col-md-pull-1">
                <div class="f-c-c-c mt-3" style="gap: 1px">
                    <span id="like-button-${param.postID}" class="like-button" data-post-id="${param.postID}">
                        <i class="fa-solid fa-thumbs-up"></i>
                    </span>
                    <span class="like-count c-light-text" id="like-count-${param.postID}"
                          data-like-count="${param.postLikes}">
                        <c:out value="${param.postLikes}"/>
                    </span>


                </div>
            </div>
            <div class="col-md-11 pt-3 pb-3 pr-3 col-md-push-11">
                <a href="${pageContext.request.contextPath}/posts/<c:out value="${param.postID}" />"
                   style="text-decoration: none;">
                    <div class="post-header">
                        <div class="blogpost-author-and-date">
                            <div class="f-r-s-c placeholder-glow" style="gap: 3px">
                                <img
                                        id="postUserProfilePictureId-${param.postID}"
                                        src=""
                                        class="small-profile-picture placeholder"
                                        alt="profile_picture_img"
                                />
                                <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>

                                <script>
                                    (function () {
                                        getImageInto("postUserProfilePictureId-${param.postID}", ${empty param.postUserProfilePictureId ? 0 : param.postUserProfilePictureId}, "${pageContext.request.contextPath}");
                                    })();
                                </script>
                                <span class="post-author pt-1 pb-1 pl-1"><c:out value="${param.postNeighborMail}"/></span>

                            </div>

                            <div style="font-size: 12px;color: var(--lighttext);">
                                <spring:message code="posted"/>
                                <span class="post-date" data-post-date="<c:out value="${param.postDate}"/>">
                                </span>
                            </div>

                        </div>
                        <h1 class="post-title"><c:out value="${param.postTitle}"/></h1>
                    </div>
                    <p class="post-description mb-2"><c:out value="${param.postDescription}"/></p>
                    <c:if test="${not empty param.postImage }">
                        <div style="display: flex; justify-content: center; align-items: center;">
                            <img class="blogpost-image"
                                 src="${pageContext.request.contextPath}/images/<c:out value="${param.postImage}"/>"
                                 alt="post_<c:out value="${param.postID}"/>_img "/>
                        </div>
                    </c:if>

                </a>
                <div class="mt-2 d-flex flex-row justify-content-start align-items-center flex-wrap">
                    <c:forEach var="postTag" items="${requestScope.postTags}">
                        <a href="${pageContext.request.contextPath}/${channel.toLowerCase()}?tag=${postTag.tag}"
                           class="post-tag static m-l-3 m-r-3" data-post-tag="${postTag.tag}">
                            <c:out value="${postTag.tag}"/>
                        </a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    (function () {

        document.addEventListener("DOMContentLoaded", function () {
            document.querySelectorAll('#like-button-' + ${param.postID}).forEach(likeButton => {
                const postId = this.getElementById('like-button-${param.postID}').getAttribute('data-post-id');
                setLikeStatus(postId);
            });
        });

        async function setLikeStatus(postId) {
            try {
                const response = await fetch('${pageContext.request.contextPath}/endpoint/is-liked?postId=' + postId);
                const isLikedResponse = await response.text();
                const likeButton = document.getElementById('like-button-' + postId);
                likeButton.setAttribute('data-liked', isLikedResponse);
                likeButton.classList.toggle('liked', isLikedResponse === 'true');
            } catch (error) {
                console.error('An error occurred:', error);
            }
        }

        let likeButtonLocked = false; // Like Semaphore

        document.getElementById('like-button-${param.postID}').addEventListener('click', async function () {
            if (likeButtonLocked) {
                return; // If the like button is locked, ignore the click event
            }
            likeButtonLocked = true;
            const liked = this.getAttribute('data-liked') === 'true';
            const postId = this.getAttribute('data-post-id'); // Get the post ID from the data attribute
            const likeEndpoint = liked ? '${pageContext.request.contextPath}/endpoint/unlike?postId=' + postId : '${pageContext.request.contextPath}/endpoint/like?postId=' + postId;

            const likeCountElement = document.getElementById('like-count-' + postId);
            const currentCount = parseInt(likeCountElement.getAttribute('data-like-count'), 10);
            const newCount = liked ? currentCount - 1 : currentCount + 1;
            this.setAttribute('data-liked', (!liked).toString());
            this.classList.toggle('liked', !liked);
            const originalLiked = liked;
            animateCount(likeCountElement, currentCount, newCount);
            likeCountElement.setAttribute('data-like-count', newCount);

            try {
                const response = await fetch(likeEndpoint, {
                    method: 'POST'
                });

            } catch (error) {
                // If the response is not OK, revert the like button to its original state
                this.setAttribute('data-liked', originalLiked.toString());
                this.classList.toggle('liked', originalLiked);
                likeCountElement.setAttribute('data-like-count', currentCount);
                console.error('An error occurred:', error);
            } finally {
                likeButtonLocked = false; // Unlock the like button after the operation is complete
            }
        });

        function animateCount(element, from, to) {
            let current = from;
            const increment = from < to ? 1 : -1;

            const interval = setInterval(function () {
                if (current === to) {
                    clearInterval(interval);
                } else {
                    current += increment;
                    element.innerText = current;
                }
            }, 50); // Adjust the interval and animation speed as needed
        }

    })();


</script>

<script src="${pageContext.request.contextPath}/resources/js/dateFormatter.js"></script>

