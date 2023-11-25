<div class="service-widget">
    <a href="${pageContext.request.contextPath}/services/profile/${worker.user.userId}">
        <div class="card-text">
            <div class="img-avatar placeholder-glow">
                <img
                        id="worker-profile-image-${worker.user.userId}"
                        src=""
                        class="big-profile-picture placeholder"
                        alt="worker_picture_img_${worker.user.userId}"
                />
                <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>

                <script>
                    (function () {
                        getImageInto("worker-profile-image-${worker.user.userId}",${empty worker.user.profilePicture.imageId ? 0 : worker.user.profilePicture.imageId}, "${pageContext.request.contextPath}")
                    })();

                </script>
            </div>

            <div class="portada placeholder-glow">

                <img
                        id="worker-background-image-${worker.user.userId}"
                        src=""
                        class="background-cover placeholder"
                        alt="worker_background_img_${worker.user.userId}"
                />
                <script>
                    (function () {
                        getImageInto("worker-background-image-${worker.user.userId}", ${empty worker.backgroundPictureId ? -1 : worker.backgroundPictureId}, "${pageContext.request.contextPath}")
                    })();

                </script>
            </div>
            <div class="title-total">
                <c:forEach items="${worker.getProfessionsAsStrings()}" var="professionString">
                    <div class="title"><spring:message code="${professionString}"/></div>
                </c:forEach>

                <h2><c:out value="${worker.user.name}"/></h2>
                <div class="desc">
                    <c:out value="${worker.bio}"/>
                </div>

            </div>
        </div>
    </a>
</div>