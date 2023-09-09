<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<%--<html>--%>
<%--<head>--%>
<%--    <link href="/css/main.css" rel="stylesheet"/>--%>
<%--</head>--%>
<%--<body>--%>
<%--<h1>Welcome!</h1>--%>
<%--<c:if test="${not empty user}">--%>
<%--    <h2>Nice to see you, <c:out value="${user.email}" escapeXml="true"/></h2>--%>
<%--    <p>Don't forget you're user id <c:out value="${user.id}" escapeXml="true"/> ;)</p>--%>
<%--</c:if>--%>
<%--<c:url var="registerUrl" value="/register"/>--%>
<%--<p>Register <a href="<c:out value="${registerUrl}" escapeXml="true"/>">here</a>.</p>--%>
<%--</body>--%>
<%--</html>--%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>Neibo</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="/css/home.css" rel="stylesheet"/>
</head>
<body>

<nav class="navbar navbar-expand-lg custom-navbar">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Neibo</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <a href="post">
                <button type="button" class="btn btn-light mr-2">post</button>
            </a>
        </div>

    </div>
</nav>

<!-- Main content divided into three columns -->
<div class="container home-container">
    <div class="column column-left" style="flex: 20%;">
        <div class="btn-group-vertical">
            <button type="button" class="btn btn-light mb-2 menu-button">
                <i class="fas fa-bullhorn"></i> Anuncios
            </button>
            <button type="button" class="btn btn-light mb-2 menu-button">
                <i class="fas fa-comments"></i> Foro
            </button>
            <button type="button" class="btn btn-light mb-2 menu-button">
                <i class="fas fa-address-card"></i> Contactos
            </button>
        </div>
    </div>
    <div class="column column-middle">
        <div class="filter-bar">
            <span>Filtrar por: </span>
        </div>
        <div class="post-box">
            <div class="post-header">
                <h5>Username/email</h5>
                <h3>Titulo</h3>
            </div>
            <div class="post-body">
                <p>Contenido del post</p>
            </div>
        </div>
    </div>



    <div class="column column-right">
        <h2>Column 3</h2>
        <div class="gray-box">
            <p>Gray Box 1</p>
        </div>
        <div class="gray-box">
            <p>Gray Box 2</p>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
</body>
</html>