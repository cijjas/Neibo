<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container ">
    <!-- Post information -->
    <div class="post-section">
        <div class="post-info">
            <p style="font-weight: bolder; font-size: 20px; color: var(--text)"><c:out value="${post.title}" /></p>
            <p style="font-size: 12px; font-weight: normal" class="m-t-40 "><spring:message code="PostedBy"/> <c:out value="${post.user.name}" /></p>
            <div class="divider m-b-20"></div>
            <div class="postcard-description">
                <c:out value="${post.description}" />
            </div>
        </div >

        <!-- Image section -->
        <c:if test="${post.postPictureId != 0}">
            <div class="placeholder-glow" style="display: flex; justify-content: center; align-items: center;">
                <img id="postImage"
                     class="blogpost-image placeholder "
                     src=""
                     style="max-width: 100%; max-height: 100vh; border-radius: 5px; height: 300px"
                     alt="post_${post.postId}_img"/>
            </div>
            <script>
                getImage();
                async function getImage() {
                    let image = document.getElementById('postImage')
                    try{

                        const response= await fetch('${pageContext.request.contextPath}/images/<c:out value="${post.postPictureId}"/>');
                        if(!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        const blob = await response.blob();
                        image.src = URL.createObjectURL(blob);
                        image.classList.remove('placeholder');
                        image.style.height = 'auto';
                    }
                    catch (e) {
                        image.src = "${pageContext.request.contextPath}/resources/images/errorImage.png";
                        console.log(e);
                    }
                }
            </script>
        </c:if>

    </div>
    <div class="m-b-20">
        <span id="post-like-button" class="like-button pl-3 pr-3 pt-2 pb-2" data-post-id="${post.postId}">
            <i class="fa-solid fa-thumbs-up"></i>
            <spring:message code="Like"/>
        </span>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const likeButton = document.getElementById('post-like-button');
            const postId = likeButton.getAttribute('data-post-id');
            setLikeStatus(postId);
        });
        async function setLikeStatus(postId){
            try{
                const response = await fetch('/api/is-liked?postId=' + postId);
                const isLikedResponse = await response.text();
                const likeButton = document.getElementById('post-like-button');
                likeButton.setAttribute('data-liked', isLikedResponse);
                likeButton.classList.toggle('liked', isLikedResponse === 'true');
            }
            catch(error){
                console.error('An error occurred:', error);
            }
        }
        document.getElementById('post-like-button').addEventListener('click', function () {
            const liked = this.getAttribute('data-liked') === 'true';
            const postId = this.getAttribute('data-post-id'); // Get the post ID from the data attribute
            const likeEndpoint = liked ? '/api/unlike?postId=' + postId : '/api/like?postId=' + postId; // Determine the appropriate API endpoint based on the like status
            fetch(likeEndpoint, {
                method: 'POST'
            })
                .then(response => {
                    if (response.ok) {
                        this.setAttribute('data-liked', (!liked).toString());
                        this.classList.toggle('liked', !liked);
                    } else {
                        console.error('Failed to like/unlike the post.');
                    }
                })
                .catch(error => {
                    console.error('An error occurred:', error);
                });
        });
    </script>


    <!-- Tag section -->
    <div class="tag-section">
        <div class="tag-list">
            <c:choose>
                <c:when test="${empty tags}">
                </c:when>
                <c:otherwise>
                    <p class="m-b-10"> <spring:message code="Tags"/> </p>
                        <div class="mt-2 d-flex flex-row justify-content-start align-items-center flex-wrap">
                            <c:forEach var="tag" items="${tags}">
                                <a class="post-tag static m-l-3 m-r-3" href="${pageContext.request.contextPath}/?tag=${tag.tag}">
                                    <c:out value="${tag.tag}" />
                                </a>
                            </c:forEach>
                        </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

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
                        <form:textarea id="commentInput" path="comment" class="cool-input grey-input"  rows="3" placeholder="${commentPlaceholder}"/>
                        <div class="form-row form-error" style="font-size: 12px; margin-left:0.5rem">
                            <form:errors path="comment" cssClass="error" element="p"/>
                        </div>
                    </div>
                    <!-- Submit button -->
                    <div class="d-flex flex-column justify-content-center align-items-end">
                        <button id="submitButton" type="submit" class="cool-button cool-small on-bg" style="margin-top:5px; font-size: 12px;" disabled>
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
                    <div class="cool-comment">
                        <div class="comment-header">
                            <strong><c:out value="${comment.user.name} ${comment.user.surname}" /></strong>
                        </div>
                        <div class="comment-body placeholder-glow">
                            <p class="placeholder col-<%= Math.round(Math.floor(Math.random() * 12) + 1) %>" id="comment-${comment.commentId}"></p>
                            <script>
                                async function getPostComment(commentId) {
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
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

</div>