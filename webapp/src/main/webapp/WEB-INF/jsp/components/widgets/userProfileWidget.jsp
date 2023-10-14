
<a href="${pageContext.request.contextPath}/profile">
    <div class="grey-container" style="width: 200px;text-overflow: ellipsis ">
        <div class="row">
            <div class="col-4 f-c-c-c pr-2">
                <div style="display: flex; justify-content: center; align-items: center;">
                    <c:choose>
                        <c:when test="${loggedUser.profilePictureId != 0}">
                            <img class="big-profile-picture" style="width: 40px; height: 40px"  src="${pageContext.request.contextPath}/images/<c:out value="${loggedUser.profilePictureId}"/>" alt="profile_picture_img" />
                        </c:when>
                        <c:otherwise>
                            <img class="big-profile-picture" style=" width: 40px; height: 40px"  src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="col-8 p-0">
                <div class="column justify-content-center align-items-start">
                    <h5 style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; width: 100%; font-size:16px">
                        <c:out value="${loggedUser.name}"/>
                    </h5>
                    <h6><c:out value="${loggedUser.surname}"/></h6>
                </div>
            </div>


        </div>
    </div>
</a>
