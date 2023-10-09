
<div class="container">

    <div class="">
        <input id="tab1" type="radio" name="tabs" checked>
        <label for="tab1">Reviews</label>

        <input id="tab2" type="radio" name="tabs">
        <label for="tab2">Posts</label>

        <input id="tab3" type="radio" name="tabs">
        <label for="tab3">Information</label>



        <section id="content1">
            <form:form method="post" action="review" modelAttribute="reviewForm">
                <!-- Message input -->
                <div class="form-group">
                    <div class="w-100">

                        <div class="w-100">
                            <spring:message code="Leave.a.review" var="commentPlaceholder"/>
                            <form:textarea id="commentInput" path="comment" class="cool-input grey-input"  rows="3" placeholder="${commentPlaceholder}"/>
                            <div class="form-row form-error" style="font-size: 12px; margin-left:0.5rem">
                                <form:errors path="comment" cssClass="error" element="p"/>
                            </div>
                        </div>
                        <!-- Submit button -->
                        <div class="d-flex flex-column justify-content-center align-items-end">
                            <button id="submitButton" type="submit" class="cool-button cool-small on-bg" style="margin-top:5px; font-size: 12px;" >
                                <spring:message code="Submit"/>
                            </button>
                        </div>
                    </div>
                </div>
                <form:errors cssClass="error" element="p"/>
            </form:form>



            <div class="container">
                <div class="f-c-c-c">
                    <div class="review-box f-c-s-s w-100">
                        <span class="font-size-16 font-weight-bold">Juan ignacio</span>
                        <span class="font-size-12">Barrio cerrado chacras</span>
                        <div class="f-r-c-c">
                            <i class="fa-solid fa-star"></i>
                            <i class="fa-solid fa-star"></i>
                            <i class="fa-solid fa-star"></i>
                            <i class="fa-regular fa-star-half-stroke"></i>
                            <i class="fa-regular fa-star"></i>
                        </div>
                        <p class="font-size-12"> El flaco es muy bueno pero la verdad tiene cara de orto y no me gusta</p>
                    </div>
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
