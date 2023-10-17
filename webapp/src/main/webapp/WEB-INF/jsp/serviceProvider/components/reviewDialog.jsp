<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>





<div class="dialog" id="reviewDialog" style="display: none; ">
    <div class="dialog-content " style="background-color: var(--onbackground)">
        <div class="close-button" onclick="closeReviewDialog()">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-4 f-c-c-c font-weight-normal" style="gap:5px">
            <spring:message code="Leave.a.review.message"/>
            <span class=" mt-2 c-primary font-weight-bolder " style="font-size: 20px">
                ${worker.businessName}
            </span>
        </div>

        <form:form method="post" action="${pageContext.request.contextPath}/service/profile/${worker.user.userId}/review" modelAttribute="reviewForm" enctype="multipart/form-data">
            <div class="f-c-c-c pl-3 pr-3 c-text">
                <form:errors cssClass="error" element="p"/>

                <input type="hidden" id="ratingInput" name="rating" value="1">
                <div class="stars m-3">
                    <i class="fa-solid fa-star active" data-value="1"></i>
                    <i class="fa-solid fa-star" data-value="2"></i>
                    <i class="fa-solid fa-star" data-value="3"></i>
                    <i class="fa-solid fa-star" data-value="4"></i>
                    <i class="fa-solid fa-star" data-value="5"></i>
                </div>
                <script>
                    const stars = document.querySelectorAll(".stars i");
                    const ratingInput = document.getElementById("ratingInput");

                    stars.forEach((star) => {
                        star.addEventListener("click", () => {
                            const ratingValue = parseInt(star.getAttribute("data-value"));
                            ratingInput.value = ratingValue; // Update the hidden input field
                            stars.forEach((s, index) => {
                                s.classList.toggle("active", index < ratingValue);
                            });
                        });
                    });
                </script>

                <div class="form-row ">
                    <spring:message code="Review" var="reviewPlaceholder"/>
                    <form:textarea path="review" class="cool-input" rows="5" placeholder="${reviewPlaceholder}"/>
                    <form:errors path="review" cssClass="error" element="p"/>
                </div>

                <button onclick="submitReviewForm()" class="cool-button cool-small on-bg font-weight-bolder mt-4" style="width: 150px">
                    <spring:message code="Send"/>
                </button>
            </div>
        </form:form>
    </div>
</div>

<script>
    function submitReviewForm() {
        document.forms["reviewForm"].submit();
    }
    function closeReviewDialog() {
        document.getElementById("reviewDialog").style.display = "none";
        const reviewForm = document.forms["reviewForm"];
        reviewForm.reset();
        const stars = reviewForm.querySelectorAll(".stars i");
        stars.forEach((star, index) => {
            star.classList.toggle("active", index === 0);
        });
        const ratingInput = reviewForm.querySelector("#ratingInput");
        ratingInput.value = "1";
    }
    function openReviewDialog() {
        document.getElementById("reviewDialog").style.display = "flex";
    }
</script>