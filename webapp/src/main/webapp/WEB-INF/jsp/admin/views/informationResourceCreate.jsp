<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="CreateResource"/></title>
</head>

<body class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row init">

        <div class="column-publish">
            <div class="cool-static-container">
                <h2 class="card-title"><spring:message code="CreateResource"/></h2>
                <div class="divider"></div>
                <form:form method="post" action="create-resource" modelAttribute="resourceForm"
                           enctype="multipart/form-data">
                    <form:errors cssClass="error" element="p"/>

                    <div class="form-column" style="margin-top:1rem;">
                        <div class="form-group">
                            <spring:message code="Title" var="titlePlaceholder"/>
                            <form:input path="title" class="cool-input" placeholder="${titlePlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="title" cssClass="error" element="p"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <spring:message code="Description" var="descriptionPlaceholder"/>
                            <form:input path="description" class="cool-input" rows="5"
                                        placeholder="${descriptionPlaceholder}"/>
                            <div class="form-row form-error">
                                <form:errors path="description" cssClass="error" element="p"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <form:label path="imageFile" for="images" class="drop-container" id="dropcontainer">
                                <span class="drop-title"> <spring:message code="Drop.files"/> </span>
                                <spring:message code="Or"/>
                                <form:input type="file" id="images" accept="image/*" path="imageFile" name="imageFile"
                                            onchange="preview()"/>
                            </form:label>
                            <div style="text-align: center">
                                <form:errors path="imageFile" cssClass="error" element="p"/>
                            </div>
                        </div>

                        <div>
                            <img id="frame" class="blogpost-image" src="" alt="uploading image" style="display: none"/>
                        </div>
                    </div>

                    <%--Submit button --%>
                    <div class="d-flex justify-content-end">
                        <button onclick="submitForm()" type="submit" class="cool-button cool-small on-bg w-25 font-weight-bold mt-2"
                                style="height:40px;"><spring:message code="Create.verb"/></button>
                    </div>

                    <script>
                        function submitForm() {
                            document.getElementById("loader-container").style.display = "flex";
                        }
                        const dropContainer = document.getElementById("dropcontainer")
                        const fileInput = document.getElementById("images")

                        dropContainer.addEventListener("dragover", (e) => {
                            // prevent default to allow drop
                            e.preventDefault()
                        }, false)

                        dropContainer.addEventListener("dragenter", () => {
                            dropContainer.classList.add("drag-active")
                        })

                        dropContainer.addEventListener("dragleave", () => {
                            dropContainer.classList.remove("drag-active")
                        })

                        dropContainer.addEventListener("drop", (e) => {
                            e.preventDefault()
                            dropContainer.classList.remove("drag-active")
                            fileInput.files = e.dataTransfer.files
                        })

                        // Show images preview
                        function preview() {

                            let frame = document.getElementById('frame');
                            if (!frame.src || frame.src === "") {
                                frame.style.display = "none";
                            } else {
                                frame.src = URL.createObjectURL(event.target.files[0]);
                                frame.style.display = "block";
                            }
                        }
                    </script>
                </form:form>
            </div>
        </div>

        <c:if test="${showSuccessMessage == true}">
            <c:set var="successMessage">
                <spring:message code="Resource.created.successfully"/>
            </c:set>

            <jsp:include page="/WEB-INF/jsp/components/widgets/successDialog.jsp">
                <jsp:param name="successMessage" value="${successMessage}"/>
            </jsp:include>
        </c:if>


        <div class="column-info">
            <%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
        </div>
    </div>
</div>

<div id="loader-container" class="loader-container ">
    <div class="cool-static-container medium-size-container">
        <%@ include file="/WEB-INF/jsp/components/widgets/placeholderBlogpost.jsp" %>

        <div style="font-weight: bold; font-size: 16px"><spring:message code="Creating.your.post"/>...</div>
        <div class="loader" style="margin-top: 20px"></div>
    </div>
</div>


<!-- Bootstrap JS and jQuery -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
<script>
    const wave3= document.getElementById('wave3');
    const wave2= document.getElementById('wave2');
    wave2.classList.add('admin');
    wave3.classList.add('admin');
</script>
</body>
</html>
