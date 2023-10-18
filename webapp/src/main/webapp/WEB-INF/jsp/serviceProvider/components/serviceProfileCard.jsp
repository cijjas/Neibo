<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<div class="container">
    <div class="service-card">
        <div class="card-header">
            <c:choose>
                <c:when test="${worker.backgroundPictureId != 0}">
                    <img class="back-image"
                         src="${pageContext.request.contextPath}/images/<c:out value="${worker.backgroundPictureId}"/>"
                         alt="profile_picture_img"/>
                </c:when>
                <c:otherwise>
                    <img class="back-image"
                         src="${pageContext.request.contextPath}/resources/images/workersBackground.png"
                         alt="default_profile_picture_img"/>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="card-body" data-profile-image='<c:choose>
            <c:when test="${worker.user.profilePictureId == 0}">
                ${pageContext.request.contextPath}/resources/images/roundedPlaceholder.png
            </c:when>
            <c:otherwise>
                ${pageContext.request.contextPath}/images/<c:out value="${worker.user.profilePictureId}"/>
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
                <p class="c-light-text mr-3 ml-3" style="text-align: center">
                    <c:out value="${worker.bio}"/>
                </p>
                <c:if test="${loggedUser.userId == worker.user.userId}">
                    <a id="editProfileButton" onclick="openEditDialog()"
                       class="cool-button cool-small on-bg font-weight-bold" style="width: 100px">
                        <spring:message code="Edit"/>
                        <i class="fa-solid fa-user-pen ml-1"></i>
                    </a>
                </c:if>
            </div>

        </div>
        <div class="card-footer">
            <c:set var="averageRating" value="${averageRating}" />
            <fmt:formatNumber var="formattedRating" value="${averageRating}" pattern="#,##0.00" />
            <div class="inner">
                <div class="c-primary"><c:out value="${formattedRating}" /></div>
                <div class="c-light-text"><spring:message code="Rating" /></div>
            </div>
            <div class="inner">
                <div class="c-primary"><c:out value="${reviewsCount}"/></div>
                <div class="c-light-text"><spring:message code="Reviews"/></div>
            </div>
            <div class="inner">
                <div class="c-primary"><c:out value="${postList.size()}"/></div>
                <div class="c-light-text"><spring:message code="Posts"/></div>
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
        <div class="title mb-2 mt-3 f-c-c-c" style="font-size: 20px">
            <spring:message code="EditProfile"/>
        </div>

        <form:form method="post" action="edit" modelAttribute="editWorkerProfileForm" enctype="multipart/form-data">
            <%--                <form:form method="post" action="calendar" modelAttribute="editWorkerProfileForm" enctype="multipart/form-data">--%>
            <div class="f-c-c-c pl-3 pr-3 c-text" style="width:500px">

                <form:errors cssClass="error" element="p"/>
                <div class="w-75 f-c-c-c" style="gap: 10px">
                    <spring:message code="Business.name" var="businessPlaceholder"/>
                    <form:input path="businessName" class="cool-input" type="text"
                                placeholder="${businessPlaceholder}"/>
                    <form:errors path="businessName" cssClass="error" element="p"/>

                    <!-- Text Input 2 -->
                    <spring:message code="Description" var="descriptionPlaceholder"/>
                    <form:input path="bio" class="cool-input" type="text" placeholder="${descriptionPlaceholder}"/>
                    <form:errors path="bio" cssClass="error" element="p"/>

                        <%--                    <!-- Phone Number Input -->--%>
                    <spring:message code="PhoneNumber" var="phonePlaceholder"/>
                    <form:input path="phoneNumber" class="cool-input" type="tel" placeholder="${phonePlaceholder}"/>
                    <form:errors path="phoneNumber" cssClass="error" element="p"/>

                        <%--                    <!-- Address Input -->       --%>
                    <spring:message code="Address" var="addressPlaceholder"/>
                    <form:input path="address" class="cool-input" type="tel" placeholder="${addressPlaceholder}"/>
                    <form:errors path="address" cssClass="error" element="p"/>
                </div>


                    <%--                    <!-- Image Input 1 (for uploading an image file) -->--%>
                <form:label path="imageFile" for="imageInput1"><spring:message code="Background.image"/></form:label>
                <form:input path="imageFile" class="cool-input" type="file" accept="image/*"/>
                <form:errors path="imageFile" cssClass="error" element="p"/>

                <button onclick="submitEditProfileForm()"
                        class="cool-button cool-small on-bg font-weight-bolder mb-4 mt-3" style="width: 150px">
                    <spring:message code="SaveChanges"/>
                </button>
            </div>

        </form:form>


        <%--        </form>--%>

    </div>
</div>

<script>
    function submitEditProfileForm() {
        document.forms["editWorkerProfileForm"].submit();
    }

    function closeEditDialog() {
        document.getElementById("editDialog").style.display = "none";
    }

    function openEditDialog() {
        document.getElementById("editDialog").style.display = "flex";
    }
</script>
