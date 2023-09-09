<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>Create a Post - Neibo</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="/css/home.css" rel="stylesheet"/>
</head>

<body>
<!-- Navigation Bar -->
<%@ include file="/WEB-INF/jsp/views/navbar.jsp" %>

<!-- Post Creation Form -->
<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <h4 class="card-title">Crear Publicacion</h4>
            <form:form method="post" action="/publish" modelAttribute="neighborPostWrapper">
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <label for="name">Nombre:</label>
                        <form:input type="text" path="neighbor.name" id="name" class="form-control rounded" />
<%--                        <form:input type="text" path="neighbor.name" id="name" /><br/>--%>
                    </div>
                    <div class="flex-grow-1">
                        <label for="surname">Apellido:</label>
<%--                        <form:input type="text" path="neighbor.surname" id="surname" /><br/>--%>
                        <form:input type="text" path="neighbor.surname" id="surname" class="form-control rounded" />

                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <label for="mail">Email:</label>
                        <form:input type="text" path="neighbor.mail" id="mail" class="form-control rounded" /><br/>
                    </div>
                    <div class="flex-grow-1">
                        <label for="neighborhood">Barrio:</label>
                        <form:input type="text" path="neighborhood.name" id="neighborhood" class="form-control rounded" /><br/>
                    </div>
                </div>

                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2" style="width: 70%;">
                        <label for="title">Sujeto:</label>
                        <form:input type="text" path="post.title" id="title" class="form-control rounded" /><br/>
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
                    <label for="description">Mensaje:</label>
                    <form:textarea path="post.description" id="description" class="form-control rounded" rows="5"/><br/>
                </div>


                <div class="d-flex justify-content-end">
                    <button type="submit" class="btn btn-primary custom-btn">Publicar</button>
                </div>

            </form:form>
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
