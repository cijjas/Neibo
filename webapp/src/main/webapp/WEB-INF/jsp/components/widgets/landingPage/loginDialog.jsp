<%--        Login dialog--%>
<div class="dialog" id="loginDialog" style="display: none">
    <div class="dialog-content">
        <div class="close-button" onclick="closeLoginDialog(); ">
            <i class="fas fa-close"></i>
        </div>
        <div class="title mb-2 mt-5 f-c-c-c" style="gap:5px">
            <spring:message code="Welcome.to.neibo"/>
            <span class=" font-weight-normal"><spring:message code="Login.to.continue"/> </span>
        </div>

        <form method="post" class="login-form" id="loginForm">

            <div class="centered-column">
                <label>
                    <c:set var="email"><spring:message code="Email"/></c:set>
                    <input type="email" placeholder="${email}" name="mail" class="input" value="${param.email}">

                </label>

                <label>
                    <c:set var="password"><spring:message code="Password"/></c:set>
                    <input type="password" placeholder="${password}" autocomplete="true" name="password" class="input">
                </label>

            </div>

            <%--REMEMBER ME CHECKBOX--%>
            <label class="centered-row light-text">
                <input name="rememberMe" type="checkbox">
                <spring:message code="Remember.me"/>
            </label>

            <div class="centered-column">

                <button class="action-button font-weight-bolder" onclick="tryLogin()">
                    <spring:message code="Login"/>
                </button>
                <span style="color:var(--lighttext); font-size: 14px;"><spring:message code="Not.a.member.question"/>
                        <a onclick="closeLoginDialog(); openSignupDialog();" class="a-link"><spring:message
                                code="Signup.now"/></a>
                </span>
                <script>
                    function tryLogin() {
                        if (document.getElementById("loginForm").checkValidity()) {
                            document.getElementById("loginForm").submit();
                        }
                    }
                </script>
            </div>

        </form>
    </div>
</div>