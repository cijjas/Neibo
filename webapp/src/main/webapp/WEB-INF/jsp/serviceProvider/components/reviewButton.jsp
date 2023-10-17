<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<button class="cool-button" onclick="openReviewDialog()"><spring:message code="CreateReview"/></button>

<div class="dialog" id="reviewDialog" style="display: none; ">
    <div class="dialog-content " style="background-color: var(--onbackground)">
        <div class="close-button" onclick="closeReviewDialog()">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-5 f-c-c-c" style="gap:5px">
            <spring:message code="CreateReview"/>
        </div>

        <form:form method="post" action="${pageContext.request.contextPath}/service/profile/${worker.user.userId}/review" modelAttribute="reviewForm" enctype="multipart/form-data">
            <div class="f-c-c pl-3 pr-3 c-text">
                <form:errors cssClass="error" element="p"/>

                <div class="form-row">
                    <form:label path="rating" for="textInput1"><spring:message code="Rating"/></form:label>
                    <form:input path="rating" class="cool-input" type="long" placeholder="Enter rating"/>
                    <form:errors path="rating" cssClass="error" element="p"/>
                </div>

                <div class="form-row">
                    <spring:message code="Review" var="reviewPlaceholder"/>
                    <form:textarea path="review" class="cool-input" rows="5" placeholder="${reviewPlaceholder}"/>
                    <form:errors path="review" cssClass="error" element="p"/>
                </div>

                <button onclick="submitReviewForm()" class="cool-button cool-small on-bg font-weight-bolder mb-4" style="width: 150px">
                    <spring:message code="Send"/>
                </button>
            </div>
        </form:form>
    </div>
</div>

<script>
    function submitReviewForm() {
        console.log("submitting review form");
        document.forms["reviewForm"].submit();
    }
    function closeReviewDialog() {
        document.getElementById("reviewDialog").style.display = "none";
    }
    function openReviewDialog() {
        document.getElementById("reviewDialog").style.display = "flex";
    }
</script>