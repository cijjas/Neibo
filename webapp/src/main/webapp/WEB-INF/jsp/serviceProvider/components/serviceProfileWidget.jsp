<div class="service-widget">
    <a href="${pageContext.request.contextPath}/service/profile/${worker.user.userId}">
        <div class="card-text">
            <div class="img-avatar">
    <%--            <c:choose>--%>
    <%--                <c:when test="${ worker.user.profilePictureId != 0}">--%>
    <%--                    <img id="output" src="${pageContext.request.contextPath}/images/<c:out value="${worker.user.profilePictureId}"/>" alt="profile_picture_img" />--%>
    <%--                </c:when>--%>
    <%--                <c:otherwise>--%>
    <%--                    <img id="output" src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />--%>
    <%--                </c:otherwise>--%>
    <%--            </c:choose>--%>
            </div>

            <div class="portada">
    <%--            <c:choose>--%>
    <%--                <c:when test="${ worker.backgroundPictureId != 0}">--%>
    <%--                    <img id="output" src="${pageContext.request.contextPath}/images/<c:out value="${worker.backgroundPictureId}"/>" alt="profile_picture_img" />--%>
    <%--                </c:when>--%>
    <%--                <c:otherwise>--%>
    <%--                    <img id="output" src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />--%>
    <%--                </c:otherwise>--%>
    <%--            </c:choose>--%>
            </div>
            <div class="title-total">
                <div class="title">
                    <span id="professionPlaceholder-${worker.user.userId}"><spring:message code="Loading."/></span>
                </div>
                <h2><c:out value="${worker.user.name}"/></h2>
                <div class="desc">
                    <c:out value="${worker.bio}"/>
                </div>
                <script>
                    async function getProfessions(workerId) {
                        try {
                            const response = await fetch("/api/profession?id=" + workerId);
                            if (!response.ok) {
                                throw new Error("Failed to fetch data from the API.");
                            }
                            const professionElement = document.getElementById("professionPlaceholder-${worker.user.userId}");
                            professionElement.textContent = await response.text(); // Set the response as text content
                        } catch (error) {
                            console.error(error.message);
                        }
                    }
                    getProfessions(${worker.user.userId});
                </script>

            </div>
        </div>
    </a>
</div>