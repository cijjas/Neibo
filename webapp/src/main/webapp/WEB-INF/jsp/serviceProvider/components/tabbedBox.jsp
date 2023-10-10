
<div class="container">

    <div class="">
        <input id="tab1" type="radio" name="tabs" checked>
        <label for="tab1"><spring:message code="Reviews"/></label>

        <input id="tab2" type="radio" name="tabs">
        <label for="tab2"><spring:message code="Posts"/></label>

        <input id="tab3" type="radio" name="tabs">
        <label for="tab3"><spring:message code="Information"/></label>



        <section id="content1">
    <%--            <form:form method="post" action="review" modelAttribute="reviewForm">--%>
    <%--                <!-- Message input -->--%>
    <%--                <div class="form-group">--%>
    <%--                    <div class="w-100">--%>

    <%--                        <div class="w-100">--%>
    <%--                            <spring:message code="Leave.a.review" var="commentPlaceholder"/>--%>
    <%--                            <form:textarea id="commentInput" path="comment" class="cool-input grey-input"  rows="3" placeholder="${commentPlaceholder}"/>--%>
    <%--                            <div class="form-row form-error" style="font-size: 12px; margin-left:0.5rem">--%>
    <%--                                <form:errors path="comment" cssClass="error" element="p"/>--%>
    <%--                            </div>--%>
    <%--                        </div>--%>
    <%--                        <!-- Submit button -->--%>
    <%--                        <div class="d-flex flex-column justify-content-center align-items-end">--%>
    <%--                            <button id="submitButton" type="submit" class="cool-button cool-small on-bg" style="margin-top:5px; font-size: 12px;" >--%>
    <%--                                <spring:message code="Submit"/>--%>
    <%--                            </button>--%>
    <%--                        </div>--%>
    <%--                    </div>--%>
    <%--                </div>--%>
    <%--                <form:errors cssClass="error" element="p"/>--%>
    <%--            </form:form>--%>



            <div class="container">
                <div class="f-c-c-c">
                    <c:forEach var="review" items="${reviews}">
                        <div class="review-box f-c-s-s w-100">
                            <span class="font-size-16 font-weight-bold" id="userNamePlaceholder"><spring:message code="Loading."/></span>
                            <span class="font-size-12" id="neighborhoodNamePlaceholder"><spring:message code="Loading."/></span>
                            <div class="f-r-c-c">
                                                    <%-- VER COMO CASTEAR FULLSTARS A INT--%>
                                <c:set var="fullStars" value="${review.rating}" />
                                <c:set var="halfStar" value="${review.rating - fullStars}" />
                                <c:set var="emptyStars" value="${5 - fullStars - (halfStar > 0 ? 1 : 0)}" />

                                <c:forEach begin="1" end="${fullStars}">
                                    <i class="fa-solid fa-star"></i>
                                </c:forEach>

                                <c:if test="${halfStar > 0}">
                                    <i class="fa-solid fa-star-half-stroke"></i>
                                </c:if>

                                <c:forEach begin="1" end="${emptyStars}">
                                    <i class="fa-regular fa-star"></i>
                                </c:forEach>
                            </div>
                            <p class="font-size-12"> <c:out value="${review.review}"/></p>
                        </div>
                        <script>
                            async function fetchUserData(reviewUserId) {
                                try {
                                    const userNameResponse = await fetch("/api/userName?id=" + reviewUserId);
                                    if (!userNameResponse.ok) {
                                        throw new Error("Failed to fetch user name from the API.");
                                    }
                                    const userNameElement = document.getElementById("userNamePlaceholder");
                                    userNameElement.textContent = await userNameResponse.text();

                                    const neighborhoodNameResponse = await fetch("/api/neighborhoodName?id=" + reviewUserId);
                                    if (!neighborhoodNameResponse.ok) {
                                        throw new Error("Failed to fetch neighborhood name from the API.");
                                    }
                                    const neighborhoodNameElement = document.getElementById("neighborhoodNamePlaceholder");
                                    neighborhoodNameElement.textContent = await neighborhoodNameResponse.text();
                                } catch (error) {
                                    console.error(error.message);
                                }
                            }
                            fetchUserData(${review.userId});
                        </script>
                    </c:forEach>
                </div>
            </div>
        </section>

        <section id="content2">
            Bacon ipsum dolor sit amet landjaeger sausage brisket, jerky drumstick fatback boudin ball tip turducken. Pork belly meatball t-bone bresaola tail filet mignon kevin turkey ribeye shank flank doner cow kielbasa shankle. Pig swine chicken hamburger, tenderloin turkey rump ball tip sirloin frankfurter meatloaf boudin brisket ham hock. Hamburger venison brisket tri-tip andouille pork belly ball tip short ribs biltong meatball chuck. Pork chop ribeye tail short ribs, beef hamburger meatball kielbasa rump corned beef porchetta landjaeger flank. Doner rump frankfurter meatball meatloaf, cow kevin pork pork loin venison fatback spare ribs salami beef ribs.
            Jerky jowl pork chop tongue, kielbasa shank venison. Capicola shank pig ribeye leberkas filet mignon brisket beef kevin tenderloin porchetta. Capicola fatback venison shank kielbasa, drumstick ribeye landjaeger beef kevin tail meatball pastrami prosciutto pancetta. Tail kevin spare ribs ground round ham ham hock brisket shoulder. Corned beef tri-tip leberkas flank sausage ham hock filet mignon beef ribs pancetta turkey.
        </section>

        <section id="content3">
            <c:out value="${worker.user.mail}"/>
            <c:out value="${worker.address}"/>
            <c:out value="${worker.user.name}"/>
        </section>


    </div>

</div>



<%----%>
