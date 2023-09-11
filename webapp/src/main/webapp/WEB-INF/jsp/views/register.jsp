<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/home.css"/>"/>
</head>
<body>
<h2>Register new user</h2>
<form:form modelAttribute="userForm" action="/register" method="post">
    <div>
        <form:errors cssClass="error" element="p"/>
<%--        Nombre de path debe matchear con los getters y setters de userForm--%>
        <form:label path="email">Email:
            <form:input path="email"/>
        </form:label>
        <form:errors path="email" cssClass="error" element="p"/>
    </div>
    <div>
        <form:label path="password">Password:
            <form:input path="password" type="password"/>
        </form:label>
        <form:errors path="password" cssClass="error" element="p"/>

    </div>
    <div>
        <form:label path="repeatPassword">Repeat Password:
            <form:input path="repeatPassword" type="password"/>
        </form:label>
        <form:errors path="repeatPassword" cssClass="error" element="p"/>
    </div>
    <div>
        <input type="submit" value="Lets go"/>
    </div>
</form:form>
<%--<form action="<c:url value="/register"/>" method="post">--%>
<%--    <div>--%>
<%--        <c:if test="${not empty email_error}">--%>
<%--            <p class="error">Invalid email</p>--%>
<%--        </c:if>--%>
<%--        <label>Email: <input type="text" placeholder="Email" name="email" value="${form.email}"></label>--%>
<%--    </div>--%>
<%--    <div>--%>
<%--        <c:if test="${not empty password_error}">--%>
<%--            <p class="error">Invalid password</p>--%>
<%--        </c:if>--%>
<%--        <label>Password: <input type="password" placeholder="Password" name="password" value="${form.password}"></label>--%>
<%--    </div>--%>
<%--    <div>--%>
<%--        <c:if test="${not empty repeatPassword_error}">--%>
<%--            <p class="error">Passwords don't match</p>--%>
<%--        </c:if>--%>
<%--        <label>Repeat password: <input type="password" placeholder="Repeat password" name="repeatPassword" value="${form.repeatPassword}"></label>--%>
<%--    </div>--%>
<%--    <div>--%>
<%--        <input type="submit" value="Register">--%>
<%--    </div>--%>
<%--</form>--%>
</body>
</html>