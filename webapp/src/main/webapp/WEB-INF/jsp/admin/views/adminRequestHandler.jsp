<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="en">


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <c:choose>
        <c:when test="${neighbors}">
            <title><spring:message code="NeighborList"/></title>
        </c:when>
        <c:otherwise>
            <title><spring:message code="Requests"/></title>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%----%>

<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>


<div class="container" >

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle">

            <c:if test="${totalPages > 1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}" />
                    <jsp:param name="totalPages" value="${totalPages}" />
                </jsp:include>
            </c:if>

            <div class="req-acc-users-list p-3" >
                <c:choose>
                    <c:when test="${empty users}">
                        <div class="user-row">
                            <div class="information">
                                <c:choose >
                                    <c:when test="${neighbors}">
                                        <spring:message code="No.verified.Users"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="No.new.requests"/>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="user" items="${users}" varStatus="loopStatus">
                            <div class="user-row">
                                <div class="information">
                                    <div>
                                        <span style="color:var(--text)"><spring:message code="Name"/>: </span>
                                        <span style="color:var(--primary)"><c:out value="${user.name}"/></span>
                                    </div>
                                    <div>

                                        <span style="color:var(--text)"><spring:message code="Surname"/>:</span>
                                        <span style="color:var(--primary)"><c:out value="${user.surname}"/></span>
                                    </div>
                                    <div>
                                        <span style="color:var(--text)"><spring:message code="Email"/>:</span>
                                        <span style="color:var(--primary)"><c:out value="${user.mail}"/></span>
                                    </div>
                                    <div>
                                        <span style="color:var(--text)"><spring:message code="Identification"/>:</span>
                                        <span style="color:var(--primary)"><c:out value="${user.identification}"/></span>
                                    </div>
                                </div>

                                <div class="acceptance">
                                    <%--Si son los neighbors solo boton de unverify--%>
                                    <c:choose >
                                        <c:when test="${neighbors}">
                                            <button class="ignore-button" onclick="unverifyUser(${user.userId})"><spring:message code="Unverify"/></button>
                                        </c:when>
                                        <c:otherwise>
                                            <%--<button class="ignore-button" onclick=""><spring:message code="Ignore"/></button>--%>
                                            <button class="cool-button cool-small" onclick="verifyUser(${user.userId})"><spring:message code="Accept"/></button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <script>

                                    function verifyUser(userId) {
                                        const verify = true;
                                        handleUserVerification(userId, verify)
                                    }
                                    function unverifyUser(userId) {
                                        const verify = false;
                                        handleUserVerification(userId, verify)
                                    }
                                    function handleUserVerification(userId, verify){
                                        console.log(verify? 'verify' : 'unverify' + ' user with id: ' + userId);
                                        const form = document.createElement('form');
                                        form.method = 'POST';
                                        if(verify){
                                            form.action = '${pageContext.request.contextPath}/admin/verify-user';
                                        }
                                        else{
                                            form.action = '${pageContext.request.contextPath}/admin/unverify-user';
                                        }
                                        const input = document.createElement('input');
                                        input.type = 'hidden';
                                        input.name = 'userId';
                                        input.value = userId;
                                        form.appendChild(input);
                                        document.body.appendChild(form);
                                        form.submit();
                                    }
                                </script>
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
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
        </div>


    </div>
</div>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>
