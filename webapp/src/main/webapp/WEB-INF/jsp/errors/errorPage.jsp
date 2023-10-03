<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page session="false"%>
<%@ page isErrorPage="true" %>
<html lang="en">

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/error.css"/>


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>Neibo - <%= request.getAttribute("javax.servlet.error.status_code") %></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
</head>
<body class="error-body">
<%----%>

    <%--<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>--%>

    <%@ include file="/WEB-INF/jsp/components/structure/backgroundDrawing.jsp" %>



    <div class="error-content-container">
        <div class="error-code">
            <c:choose>
                <c:when test="${not empty errorMsg}">
                    <c:out value="${errorCode}" />
                </c:when>
                <c:otherwise>
                    <%= request.getAttribute("javax.servlet.error.status_code") %>
                </c:otherwise>
            </c:choose>
        </div>

        <p class="error-message">
            <span class="font-weight-bold"><spring:message code="Error"/>: </span>
            <c:choose>
                <c:when test="${not empty errorMsg}">
                    <c:out value="${errorMsg}" />

                </c:when>
                <c:otherwise>
                    <%= request.getAttribute("javax.servlet.error.message") %>
                </c:otherwise>
            </c:choose>
        </p>


        <a href="${pageContext.request.contextPath}/" class="goback-button"><spring:message code="GoBackToMainPage"/></a>

    </div>

<%----%>
</body>
</html>
