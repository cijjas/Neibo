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
                <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>
                <script>
                    (function () {
                        getImageInto("worker-background-image-${worker.user.userId}", ${empty worker.backgroundPictureId ? -1 : worker.backgroundPictureId}, "${pageContext.request.contextPath}")
                    })();

                </script>
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
                            const response = await fetch("${pageContext.request.contextPath}/endpoint/profession?id=" + workerId);
                            if (!response.ok) {
                                throw new Error("Failed to fetch data from the endpoint.");
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