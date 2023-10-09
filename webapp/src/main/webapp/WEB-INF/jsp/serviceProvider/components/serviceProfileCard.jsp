

<div class="container">
    <div class="service-card">
        <div class="card-header">
            <c:choose>
                <c:when test="${loggedUser.profilePictureId != 0}">
                    <img class="back-image"   src="${pageContext.request.contextPath}/images/<c:out value="${loggedUser.profilePictureId}"/>" alt="profile_picture_img" />
                </c:when>
                <c:otherwise>
                    <img class="back-image"  src="${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png" alt="default_profile_picture_img" />
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-body" data-profile-image='${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png'>
            <div class="inner">
                <div style="font-size: 18px;margin-bottom: 10px;">
                    Emilio Maernos
                    <span class="c-light-text" style="margin-left: 10px;">45</span>
                </div>
                <div class="c-light-text" style="font-size: 13px">
                    Jardinero
                </div>
            </div>
        </div>
        <div class="card-footer">
            <div class="inner">
                <div>4.3</div>
                <div class="c-light-text">Rating</div>
            </div>
            <div class="inner">
                <div>432</div>
                <div class="c-light-text">Reviews</div>
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