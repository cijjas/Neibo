
<a href="${pageContext.request.contextPath}/profile">
    <div class="grey-container" style="width: 200px; ">
        <div class="row">
            <div class="col-5 f-c-c-c ">
                <div style="display: flex; justify-content: center; align-items: center;">
                    <c:choose>
                        <c:when test="${loggedUser.profilePictureId != 0}">
                            <img class="blogpost-image" style="border-radius: 50px;" id="output" src="${pageContext.request.contextPath}/images/<c:out value="${loggedUser.profilePictureId}"/>" alt="profile_picture_img" />
                        </c:when>
                        <c:otherwise>
                            <img class="blogpost-image" style="border-radius: 50px;" id="output" src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-7 p-0" >
                <div class="column justify-content-center align-items-start" style="text-overflow: ellipsis">
                    <h5>${loggedUser.name}</h5>
                    <h6>${loggedUser.surname}</h6>
                </div>
            </div>
        </div>
    </div>
</a>
