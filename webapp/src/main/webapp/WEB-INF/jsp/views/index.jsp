<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html lang="en">


<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <title>Neibo - ${channel}</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>


<body class="body">
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
    <div class="container" >

        <div class="row">
            <div class="column-left">
                    <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
            </div>

            <div class="column-middle">
                <%@ include file="/WEB-INF/jsp/components/upperFeedButtons.jsp" %>

                <script>
                    function showPostsAfterDelay() {
                        document.getElementById('placeholder-posts-container').style.display = 'none';
                        document.getElementById('actual-posts-container').style.display = 'block';
                    }

                    setTimeout(showPostsAfterDelay, 1000);

                </script>





                <div id="placeholder-posts-container">
                    <c:forEach begin="1" end="10" var="index">
                        <%@ include file="/WEB-INF/jsp/components/placeholderBlogpost.jsp" %>
                    </c:forEach>
                </div>

                <div id="actual-posts-container">
                    <c:choose>
                        <c:when test="${empty postList}">
                            <div class="no-posts-found">
                                <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                                <spring:message code="Posts.notFound"/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Include the page selector -->
                            <jsp:include page="/WEB-INF/jsp/components/pageSelector.jsp">
                                <jsp:param name="page" value="${page}" />
                                <jsp:param name="totalPages" value="${totalPages}" />
                            </jsp:include>
                            <c:forEach var="post" items="${postList}" >
                                <jsp:include page="/WEB-INF/jsp/components/blogpost.jsp" >
                                    <jsp:param name="postID" value="${post.postId}" />
                                    <jsp:param name="postNeighborMail" value="${post.neighbor.mail}" />
                                    <jsp:param name="postDate" value="${post.date}" />
                                    <jsp:param name="postTitle" value="${post.title}" />
                                    <jsp:param name="postDescription" value="${post.description}" />
                                    <jsp:param name="postImage" value="${post.imageFile}" />
                                </jsp:include>
                            </c:forEach>
                            <!-- Include the page selector -->
                            <jsp:include page="/WEB-INF/jsp/components/pageSelector.jsp">
                                <jsp:param name="page" value="${page}" />
                                <jsp:param name="totalPages" value="${totalPages}" />
                            </jsp:include>
                        </c:otherwise>
                    </c:choose>
                </div>



            </div>

            <div class="column-right">
                <%@ include file="/WEB-INF/jsp/components/rightColumn.jsp" %>
            </div>


        </div>
    </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

    <%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>

</html>