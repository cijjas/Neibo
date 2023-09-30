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
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="CreatePost.title"/></title>
</head>

<body  class="body ${loggedUser.darkMode ? 'dark-mode' : ''}">
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
    <div class="container">
        <div class="row">

            <div class="column-publish" >
                <div class="cool-static-container" >
                    <h2 class="card-title"><spring:message code="CreatePost.title"/></h2>
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
                                            <form:option value="${entry.value.channelId}">${entry.key}</form:option>
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
                <div id="success-container" class="success-container">
                    <div class="cool-static-container small-size-container justify-content-around ">
                        <div>
                            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 50 50" fill="none">
                                <g clip-path="url(#clip0_259_2)">
                                    <path d="M25 50C38.8071 50 50 38.8071 50 25C50 11.1929 38.8071 0 25 0C11.1929 0 0 11.1929 0 25C0 38.8071 11.1929 50 25 50Z" fill="var(--old-primary)"></path>
                                    <path d="M38 15L22 33L12 25" stroke="var(--background)" stroke-width="2" stroke-miterlimit="10" stroke-linecap="round" stroke-linejoin="round"></path>
                                </g>
                                <defs>
                                    <clipPath id="clip0_259_2">
                                        <rect width="50" height="50" fill="var(--background)"></rect>
                                    </clipPath>
                                </defs>
                            </svg>
                        </div>
                        <div style="text-align:center;font-weight: bold; font-size: 24px; color:var(--old-primary)">${successMessage}<spring:message code="Post.created.successfully"/></div>
                    </div>
                </div>
                <script>
                    // JavaScript to show the success message with fade-in effect
                    const successContainer = document.getElementById('success-container');
                    successContainer.style.display = 'flex'; // Show the container
                    setTimeout(function() {
                        successContainer.style.opacity = '1'; // Fade in
                    }, 10); // Delay for a very short time (e.g., 10ms) to trigger the transition

                    // JavaScript to hide the success message with fade-out effect after 2 seconds
                    setTimeout(function() {
                        successContainer.style.opacity = '0'; // Fade out

                        // Create a form element dynamically
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = '/redirectToChannel'; // Replace with your desired URL

                        // Optionally, you can add any form data or parameters here
                        // For example, adding a hidden input field with a value
                        const input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'channelId';
                        input.value = '${channelId}';

                        // Append the input field to the form
                        form.appendChild(input);

                        // Append the form to the document and submit it
                        document.body.appendChild(form);
                        form.submit();

                        setTimeout(function() {
                            successContainer.style.display = 'none'; // Hide the container
                        }, 500); // Wait for the transition to complete (adjust timing as needed)
                    }, 2000); // 2000 milliseconds (2 seconds)
                </script>

            </c:if>



            <div class="column-info" >
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
