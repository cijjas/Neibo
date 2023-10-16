

<div class="container">
    <div class="service-card">
        <div class="card-header">
            <c:choose>
                <c:when test="${worker.user.profilePictureId != 0}">
                    <img class="back-image" src="${pageContext.request.contextPath}/images/<c:out value="${worker.user.profilePictureId}"/>" alt="profile_picture_img" />
                </c:when>
                <c:otherwise>
                    <img class="back-image" src="${pageContext.request.contextPath}/resources/images/workersBackground.png" alt="default_profile_picture_img" />
                </c:otherwise>
            </c:choose>

        </div>
        <div class="card-body" data-profile-image='<c:choose>
            <c:when test="${worker.backgroundPictureId == 0}">
                ${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png
            </c:when>
            <c:otherwise>
                ${pageContext.request.contextPath}/images/<c:out value="${worker.backgroundPictureId}"/>
            </c:otherwise>
        </c:choose>'>
            <div class="inner">
                <div style="font-size: 18px;margin-bottom: 10px;" class="c-text">
                    <c:out value="${worker.businessName}"/>
                </div>
                <div class="c-light-text c-primary" style="font-size: 13px">
                    <c:forEach items="${professions}" var="profession" varStatus="loopStatus">
                        <c:out value="${profession}"/>
                        <c:if test="${!loopStatus.last}">,</c:if>
                    </c:forEach>
                </div>
                <span class="c-light-text" style="margin-left: 10px;"> <c:out value="${worker.phoneNumber}"/></span>
                <p  class="c-light-text mr-3 ml-3" style="text-align: center">
                    <c:out value="${worker.bio}"/>
                </p>
                <a id="editProfileButton" onclick="openEditDialog()" class="cool-button cool-small on-bg font-weight-bold" style="width: 100px">
                    <spring:message code="Edit"/>
                    <i class="fa-solid fa-user-pen ml-1"></i>
                </a>
            </div>

        </div>
        <div class="card-footer">
            <div class="inner">
                <div class="c-primary"><c:out value="${averageRating}"/></div>
                <div class="c-light-text"><spring:message code="Rating"/></div>
            </div>
            <div class="inner">
                <div class="c-primary"><c:out value="${reviewsCount}"/></div>
                <div class="c-light-text"><spring:message code="Reviews"/></div>
            </div>
            <div class="inner">
                <div class="c-primary">30</div>
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





<div class="dialog" id="editDialog" style="display: none; ">
    <div class="dialog-content " style="background-color: var(--onbackground)">
        <div class="close-button" onclick="closeEditDialog()">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-5 f-c-c-c" style="gap:5px">
            Edit your worker profile
        </div>

        <form>

            <div class="f-c-c-c pl-3 pr-3 c-text">

                <label for="textInput1"><spring:message code="Business.name"/></label>
                <input class="cool-input" type="text" id="textInput1" name="textInput1" placeholder="Enter text 1">

                <!-- Text Input 2 -->
                <label for="textInput2"><spring:message code="Description"/></label>
                <input class="cool-input" type="text" id="textInput2" name="textInput2" placeholder="Enter text 2">

                <!-- Phone Number Input -->
                <label for="phoneNumber"><spring:message code="PhoneNumber"/></label>
                <input class="cool-input" type="tel" id="phoneNumber" name="phoneNumber" placeholder="Enter phone number">

                <!-- Image Input 1 (for uploading an image file) -->
                <label for="imageInput1"><spring:message code="Background.image"/></label>
                <input class="cool-input" type="file" accept="image/*" id="imageInput1" name="imageInput1">

                <!-- Image Input 2 (for uploading an image file) -->
                <label for="imageInput2"><spring:message code="Profile.picture"/></label>
                <input class="cool-input" type="file" accept="image/*" id="imageInput2" name="imageInput2">


                <button onclick="submitSignupForm()" class="cool-button cool-small on-bg font-weight-bolder mb-4" style="width: 150px">
                    <spring:message code="SaveChanges"/>
                </button>

            </div>

        </form>

    </div>
</div>

<script>
    function closeEditDialog() {
        document.getElementById("editDialog").style.display = "none";
    }
    function openEditDialog() {
        document.getElementById("editDialog").style.display = "flex";
    }
</script>
