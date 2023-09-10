<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="/css/home.css" rel="stylesheet"/>
    <link href="/css/post.css" rel="stylesheet"/>
</head>

<body>
<!-- Navigation Bar -->
    <%@ include file="/WEB-INF/jsp/views/navbar.jsp" %>

<!-- Card container for post and comments -->
    <div class="card-container">
        <!-- Post information -->
        <div class="post-info">
            <h2>${post.title}</h2>
            <p>Publicado por: ${post.neighbor.name}</p>
            <p>Mensaje: ${post.description}</p>
        </div>

        <!-- Tag section -->
        <div class="tag">
            <p><strong> Tags :</br></strong> </p>

            <c:forEach var="tag" items="${tags}">
                <p><strong> #${tag.tag}</strong> </p>
            </c:forEach>
        </div>

        <!-- Comments section -->
        <div class="comments">

        <c:forEach var="comment" items="${comments}">
            <p><strong>User #${comment.neighborId}:</strong> ${comment.comment}</p>
        </c:forEach>
        </div>




    </div>


    <!-- Bootstrap JS and jQuery -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>
</html>
