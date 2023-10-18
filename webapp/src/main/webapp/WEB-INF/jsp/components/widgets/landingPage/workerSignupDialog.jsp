<div class="dialog" id="workerSignupDialog" style="display: none">
    <div class="dialog-content ">
        <div class="close-button" onclick="closeWorkerSignupDialog();  takeToLogin()">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-5 f-c-c-c" style="gap:5px">
            <spring:message code="Welcome.to.neibo"/>
            <span class=" font-weight-normal"><spring:message code="Signup.to.get.started"/></span>
        </div>

        <form:form method="post" action="signup-worker" modelAttribute="workerSignupForm" id="workerSignupForm">
            <form:errors cssClass="error" element="p"/>
            <div class="f-c-c-c pl-3 pr-3">
                <div class="form-input " style="max-width: 320px">
                    <form:label path="businessName">
                        <c:set var="businessName"><spring:message code="Business.name"/></c:set>
                        <form:input path="businessName" placeholder="${businessName}" class="input"/>
                    </form:label>
                    <form:errors path="businessName" cssClass="landing-error" element="p"/>
                </div>
                <div class="form-input  profession-select">
                    <form:hidden path="professionIds" id="selectedProfessions"/>

                    <div class="container">
                        <div class="select-btn">
                            <span class="btn-text"><spring:message code="Select.profession"/></span>
                            <span class="arrow-dwn">
                                        <i class="fa-solid fa-chevron-down"></i>
                                    </span>
                        </div>
                        <ul class="list-items">
                            <c:forEach var="profession" items="${professionsPairs}">
                                <li class="item" data-profession-id="${profession.key}">
                                            <span class="checkbox ">
                                                <i class="fa-solid fa-check check-icon"></i>
                                            </span>
                                    <span class="item-text"><spring:message code="${profession.value}"/></span>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                    <script>
                        const btnText = document.querySelector(".btn-text");
                        const arrow = document.querySelector(".arrow-dwn");
                        const arrowIcon = document.querySelector(".arrow-dwn i");

                        const selectBtn = document.querySelector(".select-btn"),
                            items = document.querySelectorAll(".item"),
                            selectedProfessions = document.getElementById("selectedProfessions");
                        const listContainer = document.querySelector(".list-items");

                        items.forEach(item => {
                            item.addEventListener("click", () => {
                                item.classList.toggle("checked");
                                updateSelectedProfessions();
                            });
                        });

                        function updateSelectedProfessions() {
                            const checkedItems = document.querySelectorAll(".item.checked");
                            const selectedProfessionIds = Array.from(checkedItems).map(item => item.getAttribute("data-profession-id"));
                            const selectedProfessionTexts = Array.from(checkedItems).map(item => item.querySelector(".item-text").innerText);
                            selectedProfessions.value = selectedProfessionIds.join(",");
                            const btnText = document.querySelector(".btn-text");
                            if (selectedProfessionIds.length > 0) {
                                if (selectedProfessionTexts.length > 1) {
                                    btnText.innerText = "(" + selectedProfessionIds.length + ") " + selectedProfessionTexts.join(", ");
                                } else {
                                    btnText.innerText = selectedProfessionTexts.join(", ");
                                }
                                btnText.classList.add("c-text");

                            } else {
                                btnText.classList.remove("c-text");
                                btnText.innerText = `<spring:message code="Select.profession"/>`;
                            }
                        }

                        selectBtn.addEventListener("click", () => {
                            selectBtn.classList.toggle("open");
                        });

                        // Close the list when clicking outside
                        document.addEventListener("click", (event) => {
                            if (!listContainer.contains(event.target) && event.target !== selectBtn && event.target !== btnText && event.target !== arrow && event.target !== arrowIcon) {
                                selectBtn.classList.remove("open");
                            }
                        });

                        selectBtn.addEventListener("focus", () => {
                            selectBtn.classList.add("open");
                        });


                        // Initial update of selected professions on page load
                        updateSelectedProfessions();
                    </script>
                    <form:errors path="professionIds" cssClass="landing-error" element="p"/>
                </div>

                <div class="row">
                    <div class="col-6 pr-2">
                        <div class="form-input mb-3">
                            <form:label path="w_name">
                                <c:set var="w_firstName"><spring:message code="First.name"/></c:set>
                                <form:input path="w_name" placeholder="${w_firstName}" class="input"/>
                            </form:label>
                            <form:errors path="w_name" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="w_mail">
                                <c:set var="w_email"><spring:message code="Email"/></c:set>
                                <form:input path="w_mail" placeholder="${w_email}" class="input"/>
                            </form:label>
                            <form:errors path="w_mail" cssClass="landing-error" element="p"/>
                        </div>

                        <div class="form-input mb-3">
                            <form:label path="address">
                                <c:set var="address"><spring:message code="Address"/></c:set>
                                <form:input path="address" placeholder="${address}" class="input"/>
                            </form:label>
                            <form:errors path="address" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input">
                            <form:label path="w_language">
                                <form:select path="w_language" class="cool-select">
                                    <form:option value="English"><spring:message code="English"/></form:option>
                                    <form:option value="Spanish"><spring:message code="Spanish"/></form:option>
                                </form:select>
                            </form:label>
                            <form:errors path="w_language" cssClass="landing-error" element="p"/>
                        </div>
                    </div>
                    <div class="col-6 pl-2">
                        <div class="form-input mb-3">
                            <form:label path="w_surname">
                                <c:set var="w_surname"><spring:message code="Surname"/></c:set>
                                <form:input path="w_surname" placeholder="${w_surname}" class="input"/>
                            </form:label>
                            <form:errors path="w_surname" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="w_password">
                                <c:set var="w_password"><spring:message code="Password"/></c:set>
                                <form:input type="password" path="w_password" autocomplete="true" placeholder="Password"
                                            class="input"/>
                            </form:label>
                            <form:errors path="w_password" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="w_identification">
                                <c:set var="w_identification"><spring:message code="Identification"/></c:set>

                                <form:input
                                        type="number"
                                        pattern="[0-9]*"
                                        inputmode="numeric"
                                        min="1" max="99999999"
                                        path="w_identification"
                                        placeholder="${w_identification}"
                                        class="input"
                                />
                            </form:label>
                            <form:errors path="w_identification" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="phoneNumber">
                                <c:set var="phoneNumber"><spring:message code="PhoneNumber"/></c:set>
                                <form:input path="phoneNumber" placeholder="${phoneNumber}" class="input"/>
                            </form:label>
                            <form:errors path="phoneNumber" cssClass="landing-error" element="p"/>
                        </div>

                    </div>

                </div>
                <script>
                    function submitWorkerSignupForm() {
                        const form = document.forms["workerSignupForm"];
                        form.submit();
                    }
                </script>
                <button onclick="submitWorkerSignupForm()" class="action-button font-weight-bolder mb-5"><spring:message
                        code="Signup"/></button>

            </div>

        </form:form>

    </div>
</div>