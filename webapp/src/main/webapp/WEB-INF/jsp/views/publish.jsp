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
            <h4 class="card-title">Crear Publicacion</h4>
<%--            <form:errors cssClass="error" element="p"/>--%>
<%--            <form:label path="email">Email:--%>
<%--                <form:input path="email"/>--%>
<%--            </form:label>--%>
<%--            <form:errors path="email" cssClass="error" element="p"/>--%>
            <form:form method="post" action="/publish" modelAttribute="publishForm" enctype="multipart/form-data">
                <form:errors cssClass="error" element="p"/>
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="name">Nombre:
                            <form:input path="name"/>
                        </form:label>
                        <form:errors path="name" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="surname">Apellido:
                            <form:input path="surname"/>
                        </form:label>
                        <form:errors path="surname" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <form:label path="email">Email:
                            <form:input path="email"/>
                        </form:label>
                        <form:errors path="email" cssClass="error" element="p"/>
                    </div>
                    <div class="flex-grow-1">
                        <form:label path="neighborhood">Barrio:
                            <form:input path="neighborhood"/>
                        </form:label>
                        <form:errors path="neighborhood" cssClass="error" element="p"/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2" style="width: 70%;">
                        <form:label path="subject">Sujeto:
                            <form:input path="subject"/>
                        </form:label>
                        <form:errors path="subject" cssClass="error" element="p"/>
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
                    <form:label path="message">Mensaje:
                        <form:input path="message"/>
                    </form:label>
                    <form:errors path="message" cssClass="error" element="p"/>
                </div>



                <div class="mb-3">
                    <form:label path="imageFile" class="form-label">Imagen:</form:label>
                    <form:input type="file" path="imageFile" class="form-control" onchange="preview()"/>
                </div>

                <div class="container col-md-6">
                    <img id="frame" src="" class="img-fluid"  alt="uploading image"/>
                </div>

                <script>
                    function preview() {
                        frame.src = URL.createObjectURL(event.target.files[0]);
                    }
                    function clearImage() {
                        document.getElementById('frame').value = null;
                        frame.src = "";
                    }
                </script>

                <div class="tags-input">
                    <ul id="tags"></ul>
                    <input type="text" id="input-tag"
                           placeholder="Enter tag name" />
                </div>

                <div class="d-flex justify-content-end">
                    <button onclick="clearImage()" type="submit" class="btn btn-primary custom-btn">Publicar</button>
                </div>

            </form:form>



            <script>
                // Get the tags and input elements from the DOM
                const tags = document.getElementById('tags');
                const input = document.getElementById('input-tag');

                // Add an event listener for keydown on the input element
                input.addEventListener('keydown', function (event) {

                    // Check if the key pressed is 'Enter'
                    if (event.key === 'Enter') {

                        // Prevent the default action of the keypress
                        // event (submitting the form)
                        event.preventDefault();

                        // Create a new list item element for the tag
                        const tag = document.createElement('li');

                        // Get the trimmed value of the input element
                        const tagContent = input.value.trim();

                        // If the trimmed value is not an empty string
                        if (tagContent !== '') {

                            // Set the text content of the tag to
                            // the trimmed value
                            tag.innerText = tagContent;

                            // Add a delete button to the tag
                            tag.innerHTML += '<button class="delete-button">X</button>';

                            // Append the tag to the tags list
                            tags.appendChild(tag);

                            // Clear the input element's value
                            input.value = '';
                        }
                    }
                });

                // Add an event listener for click on the tags list
                tags.addEventListener('click', function (event) {

                    // If the clicked element has the class 'delete-button'
                    if (event.target.classList.contains('delete-button')) {

                        // Remove the parent element (the tag)
                        event.target.parentNode.remove();
                    }
                });
            </script>

        </div>
    </div>

    <div class="mt-3">
        <a href="/" class="btn btn-link volver-btn"><< Volver a Inicio</a>
    </div>
</div>

<!-- Bootstrap JS and jQuery -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>
</html>
