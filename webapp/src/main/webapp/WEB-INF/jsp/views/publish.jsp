<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

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
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title>Create a post</title>
</head>

<body>
<!-- Navigation Bar -->
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

<!-- Post Creation Form -->
<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <h4 class="card-title"><spring:message code="CreatePost.title"/></h4>

            <form:form method="post" action="publish" modelAttribute="publishForm" enctype="multipart/form-data">
                <form:errors cssClass="error" element="p"/>

                <div class="form-column">

                    <div class="form-group">
                        <div class="form-input">
                            <form:label path="subject"><spring:message code="Subject"/>:</form:label>
                            <form:input path="subject" class="form-control"/>
                            <form:errors path="subject" cssClass="error" element="p"/>
                        </div>

                        <div class="form-input">
                            <form:label path="channel"><spring:message code="Channel"/>:</form:label>
                            <form:select path="channel" class="form-control">
                                <c:forEach var="entry" items="${channelList}">
                                    <form:option value="${entry.value.getChannelId()}">${entry.key}</form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="form-group">
                        <form:label path="message"><spring:message code="Message"/>:</form:label>
                        <form:textarea path="message" class="form-control" rows="5" />
                        <form:errors path="message" cssClass="error" element="p"/>
                    </div>

                    <div class="form-group">
                        <form:label path="imageFile" for="images" class="drop-container" id="dropcontainer">
                            <span class="drop-title"> <spring:message code="Drop.files"/> </span>
                            <spring:message code="Or"/>
                            <form:input type="file" id="images" accept="image/*" path="imageFile" onchange="preview()"/>

                            <form:errors path="imageFile" cssClass="error" element="p"/>
                        </form:label>
                    </div>

                    <div class="container col-md-6" display="none">
                        <img id="frame" src="" class="img-fluid"  alt="uploading image"/>
                    </div>


                    <div class="tags-input">
                        <h2>Tags Input</h2>
                        <label for="tag-input1">
                            <input type="text" id="tag-input1" >
                        </label>
                        <form:label path="tags">
                            <form:input type="hidden"  name="tags" id="tags-input" value="" path="tags"/>
                            <form:errors path="tags" cssClass="error" element="p"/>
                        </form:label>
                        <small class="text-muted">You can enter up to 5 tags.</small>
                    </div>
                </div>


                <div class="d-flex justify-content-end">
                    <button onclick="submitForm()" type="submit" class="btn btn-primary custom-btn"><spring:message code="Post.verb"/></button>
                </div>


                <script src="${pageContext.request.contextPath}/resources/js/publish.js"></script>


            </form:form>

        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/" class="btn btn-link return-btn"><spring:message code="GoBackToMainPage"/></a>
    </div>
</div>




    <!-- Bootstrap JS and jQuery -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
