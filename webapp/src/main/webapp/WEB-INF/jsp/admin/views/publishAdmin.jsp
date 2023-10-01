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
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="Create.an.announcement"/></title>
</head>


<body  class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
<div class="container">
    <div class="row">
        <div class="column-left">
            <%@ include file="/WEB-INF/jsp/admin/components/controlPanelLeftButtons.jsp" %>

        </div>

        <div class="column-middle" >
            <div class="cool-static-container" >
                <h2 class="card-title"><spring:message code="Create.an.announcement"/></h2>
                <div class="divider"></div>
                <!-- Post Creation Form -->
                <form:form method="post" action="publish" modelAttribute="publishForm" enctype="multipart/form-data">
                    <form:errors cssClass="error" element="p"/>

                    <div class="form-column" style="margin-top:1rem;">
                        <div class="form-group">
                            <div class="form-row">
                                <spring:message code="Channel" var="channelPlaceholder"/>
                                <form:select id="channel-select" path="channel" class="cool-input"  placeholder="${channelPlaceholder}">
                                    <c:forEach var="entry" items="${channelList}">
                                        <form:option value="${entry.value.channelId}"><c:out value="${entry.key}"/></form:option>
                                    </c:forEach>
                                </form:select>
                            </div>

                            <div class="form-row">

                                <spring:message code="Subject" var="subjectPlaceholder"/>
                                <form:input path="subject" class="cool-input" placeholder="${subjectPlaceholder}"/>
                                <div class="form-row form-error">
                                    <form:errors path="subject" cssClass="error" element="p"/>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <spring:message code="Message" var="messagePlaceholder"/>
                            <form:textarea path="message" class="cool-input" rows="5" placeholder="${messagePlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="message" cssClass="error" element="p"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <form:label path="imageFile" for="images" class="drop-container" id="dropcontainer">
                                <span class="drop-title"> <spring:message code="Drop.files"/> </span>
                                <spring:message code="Or"/>
                                <form:input type="file" id="images" accept="image/*" path="imageFile" name="imageFile" onchange="preview()"/>
                            </form:label>
                            <div style="text-align: center">
                                <form:errors path="imageFile" cssClass="error" element="p"/>
                            </div>
                        </div>


                        <div>
                            <img id="frame" class="blogpost-image" src=""  alt="uploading image" style="display: none"/>
                        </div>


                        <div class="tags-input" style="display:flex;flex-direction:column;align-items:center;">
                            <c:set var="val"><spring:message code="EnterATag"/></c:set>
                            <input id="niakaniaka" type="hidden" value="${val}"/>
                            <label for="tag-input1">
                                <input type="text" id="tag-input1">
                            </label>
                            <form:label path="tags" >
                                <form:input type="hidden" name="tags" id="tags-input" value="" path="tags"/>
                                <div style="text-align: center">
                                    <form:errors path="tags" cssClass="error" element="p"/>
                                </div>
                            </form:label>
                        </div>
                    </div>

                    <%--Submit button --%>
                    <div class="d-flex justify-content-end">
                        <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg" style="height:40px;" ><spring:message code="Post.verb"/></button>
                    </div>

                    <%--Tags javascript logic--%>

                    <script src="${pageContext.request.contextPath}/resources/js/tagsPublishHandler.js"></script>
                    <script src="${pageContext.request.contextPath}/resources/js/publish.js"></script>
                </form:form>
            </div>
        </div>

        <c:if test="${showSuccessMessage == true}">
            <c:set var="successMessage">
                <spring:message code="Post.created.successfully"/>
            </c:set>

            <jsp:include page="/WEB-INF/jsp/components/successDialog.jsp" >
                <jsp:param name="successMessage" value="${successMessage}" />
            </jsp:include>
        </c:if>



        <div class="column-right" >
            <%@ include file="/WEB-INF/jsp/components/calendarWidget.jsp" %>
        </div>
    </div>
</div>

<div id="loader-container" class="loader-container ">
    <div class="cool-static-container medium-size-container" >
        <%@ include file="/WEB-INF/jsp/components/placeholderBlogpost.jsp" %>

        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.your.post"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
