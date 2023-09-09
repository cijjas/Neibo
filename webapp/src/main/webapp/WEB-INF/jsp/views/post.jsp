<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

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
<nav class="navbar navbar-expand-lg custom-navbar">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Neibo</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <button type="button" class="btn btn-light mr-2">post</button>
        </div>
    </div>
</nav>

<!-- Post Creation Form -->
<div class="container mt-4">
    <div class="card">
        <div class="card-body">
            <h4 class="card-title">Crear Publicacion</h4>
            <form action="your-post-handler.jsp" method="post">
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="flex-grow-1">
                        <label for="fullName" class="form-label">Nombre completo</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" required>
                    </div>
                </div>
                <div class="d-flex mb-3">
                    <div class="flex-grow-1 mr-2" style="width: 70%;">
                        <label for="subject" class="form-label">Sujeto</label>
                        <input type="text" class="form-control" id="subject" name="subject" required>
                    </div>
                    <div class="flex-grow-1" style="width: 20%;">
                        <label for="tags" class="form-label">
                            <i class="fas fa-flag text-danger"></i> Etiqueta
                        </label>
                        <select class="form-control" id="tags" name="tags">
                            <option value="security">Seguridad</option>
                            <option value="lost">Administrativo</option>
                            <option value="administrative">Perdida</option>
                            <option value="administrative">Servicio</option>
                            <option value="administrative">Evento</option>
                            <option value="administrative">Deporte</option>
                        </select>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="body" class="form-label">Mensaje</label>
                    <textarea class="form-control" id="body" name="body" rows="5" required></textarea>
                </div>
                <div class="d-flex justify-content-start mb-3">
                    <button type="button" class="btn btn-light mr-2" title="Adjuntar Imagen">
                        <i class="fas fa-paperclip"></i>
                    </button>
                </div>


                <div class="d-flex justify-content-end">
                    <button type="submit" class="btn btn-primary custom-btn">Publicar</button>
                </div>
            </form>
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
