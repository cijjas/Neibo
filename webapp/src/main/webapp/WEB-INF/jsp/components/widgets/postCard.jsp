<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container ">
    <!-- Post information -->
    <div class="post-section">
        <div class="post-info">

            <div class="f-r-sb-c ">
                <div>
                    <span style="font-size: 14px" class="font-weight-bold">

                        <c:out value="${post.channel.channel}"/>
                    </span>
                    <span style="font-size: 12px; font-weight: normal" class="ml-1 placeholder-glow">
                        -
                        <spring:message code="PostedBy"/>
                        <img
                                id="poster-image"
                                src=""
                                class="small-profile-picture placeholder mb-1"
                                alt="poster_profile_picture_img"
                        />
                        <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>
                        <script>
                            getImageInto("poster-image", ${post.user.profilePicture.imageId}, "${pageContext.request.contextPath}");
                        </script>
                        <c:out value="${post.user.name}"/>
                    </span>


                </div>


                <div style="font-size: 12px; font-weight: normal">
                    <span class="post-date" data-post-date="<c:out value="${post.date}"/>"></span>
                </div>
                <script src="${pageContext.request.contextPath}/resources/js/dateFormatter.js"></script>

            </div>
            <p style="font-size: 20px;" class="mt-2 font-weight-bolder c-text"><c:out value="${post.title}"/></p>


            <div class="divider m-b-20"></div>
            <div class="postcard-description">
                <c:out value="${post.description}"/>
            </div>
        </div>

        <!-- Image section -->
        <c:if test="${post.postPicture.imageId != 0}">
            <div class="placeholder-glow" style="display: flex; justify-content: center; align-items: center;">
                <img id="postImage"
                     class="blogpost-image placeholder "
                     src=""
                     style="max-width: 100%; max-height: 100vh; border-radius: 5px; height: 300px"
                     alt="post_${post.postId}_img"/>
            </div>
            <script>
                getImageInto("postImage", ${post.postPicture.imageId}, "${pageContext.request.contextPath}");
            </script>
        </c:if>

    </div>

    <div class="row">
        <div class="col-6">
            <div class="ml-2 d-flex flex-column justify-content-start align-items-start">
                <div class="f-r-c-c">

                    <span id="post-like-button" class="post-like-button pl-3 pr-3 pt-2 pb-2"
                          data-post-id="${post.postId}">
                       <span class=" mr-1 " id="like-count" data-like-count="${post.likedByUsers.size()}">
                            <c:out value="${post.likedByUsers.size()}"/>
                        </span>
                        <i class="fa-solid fa-thumbs-up"></i>
                    </span>
                </div>

            </div>
        </div>
        <div class="col-6">
            <c:choose>
                <c:when test="${empty tags}">
                </c:when>
                <c:otherwise>
                    <div class="mr-2">
                        <div class="w-100 d-flex flex-row justify-content-end align-items-center flex-wrap">

                            <c:forEach var="tag" items="${tags}">
                                <a class="post-tag static m-l-3 m-r-3"
                                   href="${pageContext.request.contextPath}/?tag=${tag.tag}">
                                    <c:out value="${tag.tag}"/>
                                </a>
                            </c:forEach>
                        </div>
                    </div>


                </c:otherwise>
            </c:choose>
        </div>

    </div>


    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const likeButton = document.getElementById('post-like-button');
            const postId = likeButton.getAttribute('data-post-id');
            setLikeStatus(postId);
        });

        async function setLikeStatus(postId) {
            try {
                const response = await fetch('${pageContext.request.contextPath}/endpoint/is-liked?postId=' + postId);
                const isLikedResponse = await response.text();
                const likeButton = document.getElementById('post-like-button');
                likeButton.setAttribute('data-liked', isLikedResponse);
                likeButton.classList.toggle('liked', isLikedResponse === 'true');
            } catch (error) {
                console.error('An error occurred:', error);
            }
        }

        let likeButtonLocked = false; // Like Semaphore

        document.getElementById('post-like-button').addEventListener('click', function () {
            if (likeButtonLocked) {
                return; // If the like button is locked, ignore the click event
            }
            likeButtonLocked = true;
            const liked = this.getAttribute('data-liked') === 'true';
            const postId = this.getAttribute('data-post-id'); // Get the post ID from the data attribute
            const likeEndpoint = liked ? '${pageContext.request.contextPath}/endpoint/unlike?postId=' + postId : '${pageContext.request.contextPath}/endpoint/like?postId=' + postId;

            const likeCountElement = document.getElementById('like-count');
            const currentCount = parseInt(likeCountElement.getAttribute('data-like-count'), 10);
            const newCount = liked ? currentCount - 1 : currentCount + 1;
            this.setAttribute('data-liked', (!liked).toString());
            this.classList.toggle('liked', !liked);
            const originalLiked = liked;
            // Simulate a false count-up/down animation
            animateCount(likeCountElement, currentCount, newCount);
            // Update the like count attribute
            likeCountElement.setAttribute('data-like-count', newCount);

            fetch(likeEndpoint, {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        this.setAttribute('data-liked', (!liked).toString());
                        this.classList.toggle('liked', !liked);
                        likeButtonLocked = false;
                    } else {
                        console.error('Failed to like/unlike the post.');
                    }
                })
                .catch(error => {
                    // If the response is not OK, revert the like button to its original state
                    this.setAttribute('data-liked', originalLiked.toString());
                    this.classList.toggle('liked', originalLiked);
                    likeCountElement.setAttribute('data-like-count', currentCount);
                    console.error('An error occurred:', error);
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
        });
    </script>


    <!-- Tag section -->


    <!-- Comment section -->
    <div class="comments-section">
        <!-- Input box for adding a new comment -->

        <div style="margin: 10px 0 10px 0;">

            <form:form method="post" action="${post.postId}" modelAttribute="commentForm">
                <form:errors cssClass="error" element="p"/>

                <!-- Message input -->
                <div class="form-group">
                    <div style="width: 100%">

                        <div style="width: 100%">
                            <spring:message code="Comment" var="commentPlaceholder"/>
                            <form:textarea id="commentInput" path="comment" class="cool-input grey-input" rows="3"
                                           placeholder="${commentPlaceholder}"/>
                            <div class="form-row form-error" style="font-size: 12px; margin-left:0.5rem">
                                <form:errors path="comment" cssClass="error" element="p"/>
                            </div>
                        </div>
                        <!-- Submit button -->
                        <div class="d-flex flex-column justify-content-center align-items-end">
                            <button id="submitButton" type="submit"
                                    class="cool-button cool-small on-bg w-25 font-weight-bolder"
                                    style="margin-top:5px; font-size: 12px;" disabled>
                                <spring:message code="Comment.verb"/>
                            </button>
                        </div>
                    </div>

                </div>


                <script>
                    // Get references to the textarea and submit button
                    const commentInput = document.getElementById('commentInput');
                    const submitButton = document.getElementById('submitButton');

                    // Add an event listener to the textarea to check its content
                    commentInput.addEventListener('input', function () {
                        // Check if the textarea is empty
                        submitButton.disabled = commentInput.value.trim() === '';
                    });
                </script>

            </form:form>
        </div>
        <div class="divider"></div>

        <c:choose>
            <c:when test="${empty comments}">
                <div class="cool-comment">
                    <div class="comment-body">
                        <spring:message code="NoComments"/>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="comment" items="${comments}" varStatus="loopStatus">
                    <div class="cool-comment w-100 p-2">
                        <div class="comment-header f-r-sb-c mb-2">
                            <div class="f-r-s-c placeholder-glow">
                                <img
                                        id="comment-image-${comment.commentId}"
                                        src=""
                                        class="small-profile-picture placeholder"
                                        alt="profile_picture_img"
                                />
                                <script>
                                    (function () {
                                        getImageInto("comment-image-${comment.commentId}", ${comment.user.profilePicture.imageId}, "${pageContext.request.contextPath}");
                                    })();
                                </script>

                                <div class="bold"><c:out value="${comment.user.name} ${comment.user.surname}"/></div>

                            </div>

                            <div class="bold"><c:out value="${comment.date}"/></div>
                        </div>
                        <div class="comment-body placeholder-glow w-100">
                            <p class="mt-2 w-100 c-light-text normal placeholder col-6"
                               id="comment-${comment.commentId}"></p>
                            <script>
                                async function getPostComment(commentId) {
                                    try {
                                        const response = await fetch("${pageContext.request.contextPath}/endpoint/commentById?id=" + commentId);
                                        if (!response.ok) {
                                            throw new Error("Failed to fetch post comment data from the endpoint.");
                                        }
                                        const commentElement = document.getElementById("comment-" + commentId);
                                        commentElement.textContent = await response.text();
                                        commentElement.classList.remove('placeholder');
                                        commentElement.classList.remove('col-6');

                                    } catch (error) {
                                        console.error(error.message);
                                    }
                                }

                                getPostComment(${comment.commentId});

                            </script>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${totalPages >  1}">
                    <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                        <jsp:param name="page" value="${page}"/>
                        <jsp:param name="totalPages" value="${totalPages}"/>
                    </jsp:include>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div>

</div>