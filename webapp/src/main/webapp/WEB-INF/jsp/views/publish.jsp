<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<%@ include file="/WEB-INF/jsp/components/head.jsp" %>

<body>
<!-- Navigation Bar -->
<%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

<!-- Post Creation Form -->
<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <h4 class="card-title"><spring:message code="CreatePost.title"/></h4>
<%--            <form:errors cssClass="error" element="p"/>--%>
<%--            <form:label path="email">Email:--%>
<%--                <form:input path="email"/>--%>
<%--            </form:label>--%>
<%--            <form:errors path="email" cssClass="error" element="p"/>--%>
            <form:form method="post" action="/publish" modelAttribute="publishForm" enctype="multipart/form-data">
                <form:errors cssClass="error" element="p"/>
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="name"> <spring:message code="Name"/>:
                            <form:input path="name"/>
                        </form:label>
                        <form:errors path="name" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="surname"><spring:message code="Surname"/>:
                            <form:input path="surname"/>
                        </form:label>
                        <form:errors path="surname" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="email"><spring:message code="Email"/>:
                            <form:input path="email"/>
                        </form:label>
                        <form:errors path="email" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="neighborhood"><spring:message code="Neighborhood"/>:
                            <form:input path="neighborhood"/>
                        </form:label>
                        <form:errors path="neighborhood" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2" style="width: 70%;">
                        <form:label path="subject"><spring:message code="Subject"/>:
                            <form:input path="subject"/>
                        </form:label>
                        <form:errors path="subject" cssClass="error" element="p"/>
                    </div>

                    <div>
                        <form:label path="channel">
                            <spring:message code="Channel"/>:
                        </form:label>
                        <form:select path="channel">
                            <c:forEach var="entry" items="${channelList}">
                                <form:option value="${entry.value.getChannelId()}">
                                    ${entry.key}
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </div>
<%--                    <div class="flex-grow-1" style="width: 20%;">--%>
<%--                        <label for="tags" class="form-label">--%>
<%--                            <i class="fas fa-flag text-danger"></i> Etiqueta--%>
<%--                        </label>--%>
<%--                        <select class="form-control" id="tags" name="tags">--%>
<%--                            <option value="security">Seguridad</option>--%>
<%--                            <option value="lost">Administrativo</option>--%>
<%--                            <option value="administrative">Perdida</option>--%>
<%--                            <option value="administrative">Servicio</option>--%>
<%--                            <option value="administrative">Evento</option>--%>
<%--                            <option value="administrative">Deporte</option>--%>
<%--                        </select>--%>
<%--                    </div>--%>
                </div>
                <div class="form-group">
                    <form:label path="message"><spring:message code="Message"/>:
                        <form:input path="message"/>
                    </form:label>
                    <form:errors path="message" cssClass="error" element="p"/>
                </div>



                <div class="mb-3">
                    <form:label path="imageFile" class="form-label"><spring:message code="Image"/>:</form:label>
                    <form:input type="file" path="imageFile" class="form-control" onchange="preview()"/>
                    <form:errors path="imageFile" cssClass="error" element="p"/>
                </div>

                <div class="container col-md-6">
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



                <script>
                    let tagsString;

                    function submitForm() {
                        clearImage();
                        // Get the tags entered by the user into an array (replace this with your logic)
                        // Get the tags from tagInput1 and convert them to a string
                        const tagsArray = tagInput1.arr; // Assuming tagInput1 has an 'arr' property with the tags

                        // Convert the tagsArray to a string
                        tagsString = tagsArray.join(',');
                        // Update the hidden input field's value with the tags as a comma-separated string
                        document.getElementById('tags-input').value = tagsString;
                        //document.getElementById('submit-publishForm-button').submit();
                    }
                </script>

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

</body>
</html>
