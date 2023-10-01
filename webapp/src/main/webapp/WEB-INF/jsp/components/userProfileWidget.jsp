
<a href="${pageContext.request.contextPath}/profile">
    <div class="grey-container" style="width: 200px; ">
        <div class="row">
            <div class="col-4">
                <div style="display: flex; justify-content: center; align-items: center;">
                    <img class="blogpost-image" src="${pageContext.request.contextPath}/images/<c:out value="${neighbor.profilePictureId}"/>" alt="profile_picture_img" />
                </div>
            </div>
            <div class="col-8">
                <div class="column justify-content-center align-items-start">
                    <h5>${loggedUser.name}</h5>
                    <h6>${loggedUser.surname}</h6>
                </div>
            </div>
        </div>
    </div>
</a>
