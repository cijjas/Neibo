<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html lang="en">

<%@ include file="/WEB-INF/jsp/components/head.jsp" %>


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