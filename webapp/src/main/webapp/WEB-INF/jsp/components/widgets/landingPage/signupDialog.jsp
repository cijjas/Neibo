<div class="dialog" id="signupDialog" style="display: none">
    <div class="dialog-content " >
        <div class="close-button" onclick="closeSignupDialog()">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-5 f-c-c-c" style="gap:5px">
            <spring:message code="Welcome.to.neibo"/>
            <span class=" font-weight-normal"><spring:message code="Signup.to.get.started"/></span>
        </div>


        <form:form method="post" action="signup" modelAttribute="signupForm" id="signupForm">
            <form:errors cssClass="error" element="p"/>

            <div class="f-c-c-c pl-3 pr-3">
                <div class="form-input align-items-start " style="gap:5px; width: 200px">
                    <span class="font-weight-normal c-light-text ml-2"><spring:message code="Neighborhood"/></span>
                    <form:select path="neighborhoodId" class="cool-select">
                        <c:forEach var="entry" items="${neighborhoodsList}">
                            <form:option value="${entry.getNeighborhoodId()}"><c:out value="${entry.getName()}"/></form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="row">
                    <div class="col-6 pr-2" >
                        <div class="form-input mb-3">
                            <form:label path="name">
                                <c:set var="firstName"><spring:message code="First.name"/></c:set>
                                <form:input path="name" placeholder="${firstName}" class="input"/>
                            </form:label>
                            <form:errors path="name" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="mail">
                                <c:set var="email"><spring:message code="Email"/></c:set>
                                <form:input path="mail" placeholder="${email}" class="input"/>
                            </form:label>
                            <form:errors path="mail" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input ">
                            <form:label path="identification">
                                <c:set var="identification"><spring:message code="Identification"/></c:set>

                                <form:input
                                        type="number"
                                        pattern="[0-9]*"
                                        inputmode="numeric"
                                        min="1" max="99999999"
                                        path="identification"
                                        placeholder="${identification}"
                                        class="input"
                                />
                            </form:label>
                            <form:errors path="identification" cssClass="landing-error" element="p"/>
                        </div>
                    </div>
                    <div class="col-6 pl-2">
                        <div class="form-input mb-3">
                            <form:label path="surname">
                                <c:set var="surname"><spring:message code="Surname"/></c:set>
                                <form:input path="surname" placeholder="${surname}" class="input"/>
                            </form:label>
                            <form:errors path="surname" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input mb-3">
                            <form:label path="password">
                                <c:set var="password"><spring:message code="Password"/></c:set>
                                <form:input type="password" path="password" autocomplete="true" placeholder="Password" class="input"/>
                            </form:label>
                            <form:errors path="password" cssClass="landing-error" element="p"/>
                        </div>
                        <div class="form-input ">
                            <form:label path="language">
                                <form:select path="language" class="cool-select" >
                                    <form:option value="English"><spring:message code="English"/></form:option>
                                    <form:option value="Spanish"><spring:message code="Spanish"/></form:option>
                                </form:select>
                            </form:label>
                            <form:errors path="language" cssClass="landing-error" element="p"/>
                        </div>

                    </div>
                </div>


                <a onclick="submitSignupForm()" class="action-button font-weight-bolder mt-3"><spring:message code="Signup"/></a>

            </div>
            <div class="mt-3 mb-5">
                <span style="color:var(--lighttext); font-size: 14px;"><spring:message code="Worker.signup.question"/>
                            <a onclick="closeSignupDialog(); openWorkerSignupDialog();" class="a-link"><spring:message code="Signup.here"/></a>
                </span>
            </div>

        </form:form>

    </div>
</div>