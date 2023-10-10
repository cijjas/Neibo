

<div class="container">
    <div class="service-card">
        <div class="card-header">
            <c:choose>
                <c:when test="${worker.user.profilePictureId != 0}">
                    <img class="back-image"   src="${pageContext.request.contextPath}/images/<c:out value="${worker.user.profilePictureId}"/>" alt="profile_picture_img" />
                </c:when>
                <c:otherwise>
                    <img class="back-image"  src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-body" data-profile-image='${pageContext.request.contextPath}/images/<c:out value="${worker.backgroundPictureId}"/>'>
            <div class="inner">
                <div style="font-size: 18px;margin-bottom: 10px;">
                    <c:out value="${worker.businessName}"/>
                </div>
                <div class="c-light-text c-primary" style="font-size: 13px">
                    <c:out value="${profession}"/>
                </div>
                <span class="c-light-text" style="margin-left: 10px;"> <c:out value="${worker.phoneNumber}"/></span>
                <p  class="c-light-text mr-3 ml-3" style="text-align: center">
                    <c:out value="${worker.bio}"/>
                </p>
            </div>
        </div>
        <div class="card-footer">
            <div class="inner">
                <div><c:out value="${averageRating}"/></div>
                <div class="c-light-text"><spring:message code="Rating"/></div>
            </div>
            <div class="inner">
                <div><c:out value="${reviewsCount}"/></div>
                <div class="c-light-text"><spring:message code="Reviews"/></div>
            </div>
            <div class="inner">
                <div>30</div>
                <div class="c-light-text">Photos</div>
            </div>
        </div>
    </div>
</div>



<script>
    const cardBodies = document.querySelectorAll('.card-body');

    cardBodies.forEach((cardBody) => {
        // Get the background image URL from the data attribute
        const backgroundImage = cardBody.getAttribute('data-profile-image');

        // Set the background image for the ::before pseudo-element
        cardBody.style.setProperty('--background-image', 'url(' + backgroundImage + ')');
    });
</script>