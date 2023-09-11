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
<body class="body">
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>




    <div class="container ">
        <div class="row">
            <div class="column column-left">
                <%@ include file="/WEB-INF/jsp/components/leftcolumn.jsp" %>
            </div>

            <div class="column column-middle">
                <div class="filter-options d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/?sortBy=dateasc" style="text-decoration: none;">
                        <button id="filter-by-date" class="filter-button" onclick="">
                            <i class="fa-solid fa-newspaper"></i>
                            <span>Fecha</span>
                        </button>
                    </a>

                    <button id="filter-by-tag" class="filter-button">
                        <a class="dropdown-toggle" href="#" id="dropdown07XL" data-bs-toggle="dropdown" aria-expanded="false">Tag</a>
                        <ul class="dropdown-menu" aria-labelledby="filter-by-tag">
                            <c:forEach var="tag" items="${tagList}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/?sortBy=tag${tag.tag}" data-tag="${tag.tag}">#${tag.tag}</a></li>
                            </c:forEach>
                        </ul>
                    </button>

                    <a href="${pageContext.request.contextPath}/publish" style="text-decoration: none;">
                        <button id="create-post-button" class="filter-button">
                            Crear publicación
                            <i class="fa-solid fa-plus"></i>
                        </button>
                    </a>
                </div>


                <c:forEach var="post" items="${postList}">
                    <jsp:include page="/WEB-INF/jsp/components/blogpost.jsp">
                        <jsp:param name="postID" value="${post.postId}" />
                        <jsp:param name="postNeighborMail" value="${post.neighbor.mail}" />
                        <jsp:param name="postDate" value="${post.date}" />
                        <jsp:param name="postTitle" value="${post.title}" />
                        <jsp:param name="postDescription" value="${post.description}" />
                        <jsp:param name="postImage" value="${post.imageFile}" />
                    </jsp:include>
                </c:forEach>



            </div>

            <div class="column column-right">
                <%@ include file="/WEB-INF/jsp/components/rightcolumn.jsp" %>
            </div>


        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>
</html>