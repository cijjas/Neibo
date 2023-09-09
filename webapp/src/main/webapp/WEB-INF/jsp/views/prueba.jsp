<%--
  Created by IntelliJ IDEA.
  User: chrij
  Date: 9/9/2023
  Time: 11:12 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<%--@ page contentType="text/html;charset=UTF-8" language="java" --%>

<html lang="en">
<head>
    <title>Neibo - Conectados es mejor</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/home.css" rel="stylesheet"/>

</head>
<body>

    <%@ include file="/WEB-INF/jsp/views/navbar.jsp" %>


<!-- Main content divided into three columns -->
<div class="container">


    <div class="column column-left" style="flex: 20%; background-color: transparent;">
        <div class="btn-group-vertical">
            <button type="button" class="btn btn-light mb-2" style="background-color: transparent;">
                <i class="fas fa-bullhorn"></i> Anuncios
            </button>
            <button type="button" class="btn btn-light mb-2">
                <i class="fas fa-comments"></i> Foro
            </button>
            <button type="button" class="btn btn-light mb-2">
                <i class="fas fa-address-card"></i> Contactos
            </button>
        </div>
    </div>
    <div class="column column-middle" style="flex: 60%;">
        <h2>Main Content</h2>
        <p>Aca se verian los posts</p>
        <div class="box">Box in the middle column</div>
    </div>
    <div class="column column-right" style="flex: 20%;">
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
