
<div class="container">

    <div class="">
        <input id="tab1" type="radio" name="tabs" checked>
        <label for="tab1"><spring:message code="Reviews"/></label>

        <input id="tab2" type="radio" name="tabs">
        <label for="tab2"><spring:message code="Posts"/></label>

        <input id="tab3" type="radio" name="tabs">
        <label for="tab3"><spring:message code="Information"/></label>



        <section id="content1">
    <%--            <form:form method="post" action="review" modelAttribute="reviewForm">--%>
    <%--                <!-- Message input -->--%>
    <%--                <div class="form-group">--%>
    <%--                    <div class="w-100">--%>

    <%--                        <div class="w-100">--%>
    <%--                            <spring:message code="Leave.a.review" var="commentPlaceholder"/>--%>
    <%--                            <form:textarea id="commentInput" path="comment" class="cool-input grey-input"  rows="3" placeholder="${commentPlaceholder}"/>--%>
    <%--                            <div class="form-row form-error" style="font-size: 12px; margin-left:0.5rem">--%>
    <%--                                <form:errors path="comment" cssClass="error" element="p"/>--%>
    <%--                            </div>--%>
    <%--                        </div>--%>
    <%--                        <!-- Submit button -->--%>
    <%--                        <div class="d-flex flex-column justify-content-center align-items-end">--%>
    <%--                            <button id="submitButton" type="submit" class="cool-button cool-small on-bg" style="margin-top:5px; font-size: 12px;" >--%>
    <%--                                <spring:message code="Submit"/>--%>
    <%--                            </button>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                </div>--%>
    <%--                <form:errors cssClass="error" element="p"/>--%>
    <%--            </form:form>--%>



            <div class="container">
                <div class="f-c-c-c">
                    <c:forEach var="review" items="${reviews}" varStatus="loopStatus">
                        <div class="review-box f-c-s-s w-100">
                            <span class="font-size-16 font-weight-bold" id="userNamePlaceholder-${loopStatus.index}"><spring:message code="Loading."/></span>
                            <span class="font-size-12" id="neighborhoodNamePlaceholder-${loopStatus.index}"><spring:message code="Loading."/></span>
                            <div class="f-r-c-c">
                                <div id="starsContainer-${loopStatus.index}">
                                    <!-- Container for stars -->
                                </div>

                            </div>
                            <p class="font-size-12"> <c:out value="${review.review}"/></p>
                        </div>
                        <script>
                            async function fetchUserData(reviewUserId) {
                                try {
                                    const userNameResponse = await fetch("/endpoint/userName?id=" + reviewUserId);
                                    if (!userNameResponse.ok) {
                                        throw new Error("Failed to fetch user name from the endpoint.");
                                    }
                                    const userNameElement = document.getElementById("userNamePlaceholder-${loopStatus.index}");
                                    userNameElement.textContent = await userNameResponse.text();

                                    const neighborhoodNameResponse = await fetch("/endpoint/neighborhoodName?id=" + reviewUserId);
                                    if (!neighborhoodNameResponse.ok) {
                                        throw new Error("Failed to fetch neighborhood name from the endpoint.");
                                    }
                                    const neighborhoodNameElement = document.getElementById("neighborhoodNamePlaceholder-${loopStatus.index}");
                                    neighborhoodNameElement.textContent = await neighborhoodNameResponse.text();

                                    // Calculate rating as stars
                                    const fullStars = Math.floor(${review.rating});
                                    const halfStar = ${review.rating} - fullStars;
                                    const emptyStars = 5 - fullStars - (halfStar > 0 ? 1 : 0);

                                    const starsContainer = document.querySelector("#starsContainer-${loopStatus.index}");
                                    starsContainer.innerHTML = "";

                                    for (let i = 1; i <= fullStars; i++) {
                                        starsContainer.innerHTML += '<i class="fa-solid fa-star"></i>';
                                    }

                                    if (halfStar > 0) {
                                        starsContainer.innerHTML += '<i class="fa-solid fa-star-half-stroke"></i>';
                                    }

                                    for (let i = 1; i <= emptyStars; i++) {
                                        starsContainer.innerHTML += '<i class="fa-regular fa-star"></i>';
                                    }
                                } catch (error) {
                                    console.error(error.message);
                                }
                            }
                            fetchUserData(${review.userId});
                        </script>
                    </c:forEach>
                </div>
            </div>
        </section>

        <section id="content2">
            <div id="actual-posts-container">
                <c:choose>
                    <c:when test="${empty postList}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass" style="color:var(--text)"></i>
                            <spring:message code="Posts.notFound"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Include the page selector -->
                        <c:if test="${totalPages >  1}">
                            <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                                <jsp:param name="page" value="${page}" />
                                <jsp:param name="totalPages" value="${totalPages}" />
                            </jsp:include>
                        </c:if>

                        <c:forEach var="post" items="${postList}" >
                            <c:set var="postTags" value="${post.tags}" scope="request"/>
                            <jsp:include page="/WEB-INF/jsp/components/widgets/blogpost.jsp" >
                                <jsp:param name="postID" value="${post.postId}" />
                                <jsp:param name="postNeighborMail" value="${post.user.mail}" />
                                <jsp:param name="postDate" value="${post.date}" />
                                <jsp:param name="postTitle" value="${post.title}" />
                                <jsp:param name="postDescription" value="${post.description}" />
                                <jsp:param name="postImage" value="${post.postPictureId}" />
                                <jsp:param name="postLikes" value="${post.likes}" />
                            </jsp:include>
                        </c:forEach>


                        <c:if test="${totalPages >  1}">
                            <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                                <jsp:param name="page" value="${page}" />
                                <jsp:param name="totalPages" value="${totalPages}" />
                            </jsp:include>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </section>

        <section id="content3">
            <c:out value="${worker.user.mail}"/>
            <c:out value="${worker.address}"/>
            <c:out value="${worker.user.name}"/>
        </section>


    </div>

</div>



<%----%>
