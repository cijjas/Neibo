<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title><spring:message code="Verify.users"/></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>
<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%----%>

<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>


<div class="container" >

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
        </div>

        <div class="column-middle">

            <div class="req-acc-users-list" >
                <c:choose>
                    <c:when test="${empty unverifiedList}">
                        <div class="user-row">
                            <div class="information">
                                <spring:message code="No.new.requests"/>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="user" items="${unverifiedList}" varStatus="loopStatus">
                            <div class="user-row">
                                <div class="information">
                                    <div>
                                        <span style="color:var(--primary)"><spring:message code="Name"/>: </span>
                                        <span style="color:var(--old-primary)"><c:out value="${user.name}"/></span>
                                    </div>
                                    <div>

                                        <span style="color:var(--primary)"><spring:message code="Surname"/>:</span>
                                        <span style="color:var(--old-primary)"><c:out value="${user.surname}"/></span>
                                    </div>
                                    <div>
                                        <span style="color:var(--primary)"><spring:message code="Email"/>:</span>
                                        <span style="color:var(--old-primary)"><c:out value="${user.mail}"/></span>
                                    </div>
                                </div>

                                <div class="acceptance">
                                    <button class="ignore-button" onclick=""><spring:message code="Ignore"/></button>
                                    <button class="cool-button cool-small" onclick=""><spring:message code="Accept"/></button>
                                </div>
                            </div>
                            <c:if test="${not loopStatus.last}">
                                <div class="divider"></div>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

            </div>

        </div>

        <div class="column-right">
            <div class="req-acc-users-list" >
                <c:choose>
                    <c:when test="${empty verifiedList}">
                        <div class="user-row">
                            <div class="information">
                                <spring:message code="No.verified.Users"/>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>

                        <c:forEach var="user" items="${verifiedList}" varStatus="loopStatus">
                            <div class="user-column  m-2">
                                <div class="flex-column-centerd">
                                    <div class="information">
                                        <div>
                                            <span style="color:var(--primary)"><spring:message code="Name"/>: </span>
                                            <span style="color:var(--old-primary)"><c:out value="${user.name}"/></span>
                                        </div>
                                        <div>

                                            <span style="color:var(--primary)"><spring:message code="Surname"/>:</span>
                                            <span style="color:var(--old-primary)"><c:out value="${user.surname}"/></span>
                                        </div>
                                        <div>
                                            <span style="color:var(--primary)"><spring:message code="Email"/>:</span>
                                            <span style="color:var(--old-primary)"><c:out value="${user.mail}"/></span>
                                        </div>

                                    </div>

                                </div>
                                <div class="acceptance">
                                    <button class="ignore-button" onclick=""><spring:message code="Unverify"/></button>
                                </div>
                            </div>
                            <c:if test="${not loopStatus.last}">
                                <div class="divider"></div>
                            </c:if>
                        </c:forEach>

                    </c:otherwise>
                </c:choose>

            </div>
        </div>


    </div>
</div>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
