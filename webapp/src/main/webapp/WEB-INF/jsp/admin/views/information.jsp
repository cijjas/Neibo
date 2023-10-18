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
    <title><spring:message code="Information"/></title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>


<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">

    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>
        </div>

        <div class="column-middle">
            <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">

                <p class="mb-3" style="color:var(--lighttext);"><spring:message code="ContactInformation"/></p>
                <c:choose>
                    <c:when test="${empty phoneNumbersList}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                            <spring:message code="Information.not.found"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex flex-column justify-content-center align-items-center w-100">
                            <table class="table-striped w-100">
                                <tr>
                                    <th class="day"><spring:message code="Name"/></th>
                                    <th><spring:message code="Address"/></th>
                                    <th><spring:message code="PhoneNumber"/></th>
                                    <th></th>
                                </tr>
                                <c:forEach var="contact" items="${phoneNumbersList}">
                                    <tr>
                                        <td class="day"><c:out value="${contact.contactName}"/></td>
                                        <td><c:out value="${contact.contactAddress}"/></td>
                                        <td><c:out value="${contact.contactPhone}"/></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/admin/delete-contact/${contact.contactId}"
                                               class="btn btn-link">
                                                <i class="fas fa-trash" style="color: var(--error);"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
                <div class="add-button m-t-40">
                    <a href="${pageContext.request.contextPath}/admin/create-contact">
                        <button class="cool-button cool-small on-bg">
                            <spring:message code="Add"/> <i class="fas fa-plus"></i>
                        </button>
                    </a>
                </div>
            </div>
            <div class="upper-feed-buttons-box m-b-20">
                <a class="cool-feed-button" href="${pageContext.request.contextPath}/admin/create-resource">
                    <spring:message code="AddResource"/>
                    <i class="fa-solid fa-plus"></i>
                </a>
            </div>

            <c:forEach var="resource" items="${resourceList}">
                <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                    <div class="row">
                        <div class="col-md-8">
                            <h2><c:out value="${resource.title}"/></h2>
                        </div>
                        <div class="col-md-4 text-right">
                            <a href="${pageContext.request.contextPath}/admin/delete-resource/${resource.resourceId}"
                               class="btn btn-link">
                                <i class="fas fa-trash" style="color: var(--error);"></i>
                            </a>
                        </div>
                    </div>
                    <div class="divider"></div>
                    <h3 style="font-weight: normal "><c:out value="${resource.description}"/></h3>

                    <div class="d-flex flex-column justify-content-center align-items-center w-100">

                        <c:if test="${resource.imageId != 0}">
                            <div style="display: flex; justify-content: center; align-items: center;">
                                <img src="${pageContext.request.contextPath}/images/<c:out value="${resource.imageId}" />"
                                     style="max-width: 100%; max-height: 100vh; border-radius: 5px;"
                                     alt="resource_${resource.imageId}_img"/>
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>

        </div>

        <div class="column-right">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
        </div>


    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>

<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>

</body>

</html>