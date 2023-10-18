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
                        getWorkerProfilePicture();

                        async function getWorkerProfilePicture() {
                            let image = document.getElementById('worker-profile-image-' + ${worker.user.userId})
                            if ("${worker.user.profilePictureId}" === "0") {
                                image.src = "${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png";
                                image.classList.remove('placeholder');
                                return;
                            }
                            try {
                                const response = await fetch('${pageContext.request.contextPath}/images/<c:out value="${worker.user.profilePictureId}"/>');
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                const blob = await response.blob();

                                setTimeout(() => {
                                    image.classList.remove('placeholder');
                                    image.src = URL.createObjectURL(blob);
                                }, 3000);

                            } catch (e) {
                                image.src = "${pageContext.request.contextPath}/resources/images/errorImage.png";
                                console.log(e);
                            }
                        }
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
                        getWorkerBackgroundPicture();

                        async function getWorkerBackgroundPicture() {
                            let image = document.getElementById('worker-background-image-' + ${worker.user.userId})
                            if ("${worker.backgroundPictureId}" === "0") {
                                image.src = "${pageContext.request.contextPath}/resources/images/workersBackground.png";
                                image.classList.remove('placeholder');
                                return;
                            }
                            try {
                                const response = await fetch('${pageContext.request.contextPath}/images/<c:out value="${worker.backgroundPictureId}"/>');
                                if (!response.ok) {
                                    throw new Error('Network response was not ok');
                                }
                                const blob = await response.blob();

                                setTimeout(() => {
                                    image.classList.remove('placeholder');
                                    image.src = URL.createObjectURL(blob);
                                }, 3000);

                            } catch (e) {
                                image.src = "${pageContext.request.contextPath}/resources/images/errorImage.png";
                                console.log(e);
                            }
                        }
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
                            const response = await fetch("/endpoint/profession?id=" + workerId);
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