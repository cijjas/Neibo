<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html lang="en">


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <c:choose>
        <c:when test="${inWorkers}">
            <title><spring:message code="Worker.list"/></title>
        </c:when>
        <c:otherwise>
            <title><spring:message code="Worker.requests"/></title>
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


<div class="container">

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle">

            <c:if test="${inWorkers}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/rejectedSwitch.jsp">
                    <jsp:param name="verified" value="${verified}"/>
                </jsp:include>
            </c:if>

            <div class="req-acc-users-list p-3">
                <c:choose>
                    <c:when test="${empty workers}">
                        <div class="user-row">
                            <div class="information">
                                <c:choose>
                                    <c:when test="${inWorkers}">
                                        <spring:message code="No.verified.workers"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="No.new.requests"/>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="worker" items="${workers}" varStatus="loopStatus">
                            <div class="user-row">
                                <div class="f-r-s-c placeholder-glow">
                                    <div>
                                        <img
                                                id="user-profile-image-${worker.user.userId}"
                                                src=""
                                                class="big-profile-picture placeholder"
                                                style="width: 50px; height: 50px"
                                                alt="user_picture_img_${worker.user.userId}"
                                        />
                                        <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>

                                        <script>
                                            (function () {
                                                getImageInto("user-profile-image-${worker.user.userId}", ${empty user.profilePicture.imageId ? 0 : user.profilePicture.imageId}, "${pageContext.request.contextPath}");
                                            })();
                                        </script>
                                    </div>
                                    <div class="information">
                                        <div>
                                            <span style="color:var(--text)"><spring:message code="Name"/>: </span>
                                            <span style="color:var(--primary)"><c:out value="${worker.user.name}"/></span>
                                        </div>
                                        <div>

                                            <span style="color:var(--text)"><spring:message code="Surname"/>:</span>
                                            <span style="color:var(--primary)"><c:out value="${worker.user.surname}"/></span>
                                        </div>
                                        <div>
                                            <span style="color:var(--text)"><spring:message code="Email"/>:</span>
                                            <span style="color:var(--primary)"><c:out value="${worker.user.mail}"/></span>
                                        </div>
                                        <div>
                                            <span style="color:var(--text)"><spring:message
                                                    code="Identification"/>:</span>
                                            <span style="color:var(--primary)"><c:out
                                                    value="${worker.user.identification}"/></span>
                                        </div>
                                        <div>
                                            <span style="color:var(--text)"><spring:message
                                                    code="PhoneNumber"/>:</span>
                                            <span style="color:var(--primary)"><c:out
                                                    value="${worker.phoneNumber}"/></span>
                                        </div>
                                        <div>
                                            <span style="color:var(--text)"><spring:message
                                                    code="Business.name"/>:</span>
                                            <span style="color:var(--primary)"><c:out
                                                    value="${worker.businessName}"/></span>
                                        </div>
                                    </div>
                                </div>

                                <div class="acceptance">
                                        <%--If its only workers display the unverify button--%>
                                    <c:choose>
                                        <c:when test="${inWorkers}">
                                            <c:choose>
                                                <c:when test="${verified}">
                                                    <button class="ignore-button  outlined on-bg"
                                                            onclick="rejectWorker(${worker.workerId})">
                                                        <spring:message code="Remove"/>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="cool-button cool-small font-weight-bold on-bg p-2"
                                                            onclick="verifyWorker(${worker.workerId})">
                                                        <spring:message code="Accept"/>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <button class="ignore-button outlined on-bg"
                                                    onclick="rejectWorker(${worker.workerId})"><spring:message
                                                    code="Reject"/></button>
                                            <button class="cool-button cool-small font-weight-bold on-bg p-2"
                                                    onclick="verifyWorker(${worker.workerId})"><spring:message
                                                    code="Accept"/></button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <script>

                                    function verifyWorker(workerId) {
                                        const verify = true;
                                        handleWorkerVerification(workerId, verify)
                                    }

                                    function rejectWorker(workerId) {
                                        const verify = false;
                                        handleWorkerVerification(workerId, verify)
                                    }

                                    function handleWorkerVerification(workerId, verify) {
                                        const form = document.createElement('form');
                                        form.method = 'POST';
                                        if (verify) {
                                            form.action = '${pageContext.request.contextPath}/admin/verify-worker';
                                        } else {
                                            form.action = '${pageContext.request.contextPath}/admin/reject-worker';
                                        }
                                        const input = document.createElement('input');
                                        input.type = 'hidden';
                                        input.name = 'workerId';
                                        input.value = workerId;
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

            <c:if test="${totalPages > 1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}"/>
                    <jsp:param name="totalPages" value="${totalPages}"/>
                </jsp:include>
            </c:if>

        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
        </div>


    </div>
</div>


<%----%>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous">

</script>

<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>
</html>
