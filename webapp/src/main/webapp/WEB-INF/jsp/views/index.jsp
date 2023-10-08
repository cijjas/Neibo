<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
</head>

<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">


<div style=" position: absolute; width: 100%; height: 100%; top:0; left:0; z-index: 1">
   <div class="w-100">
        <svg style=" position: absolute; top:60%; left:10%" width="618" height="217" viewBox="0 0 618 217" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path fill-rule="evenodd" clip-rule="evenodd" d="M76.493 99.851C79.621 99.851 82.705 100.038 85.735 100.403C98.136 75.843 123.595 59 152.988 59C174.343 59 193.621 67.891 207.324 82.172C219.651 75.564 233.74 71.816 248.705 71.816C297.145 71.816 336.413 111.084 336.413 159.524C336.413 165.136 335.886 170.625 334.879 175.944H0C0.216 133.882 34.38 99.851 76.493 99.851Z" fill="#FDFCFB"/>
            <g filter="url(#filter0_d_0_1)">
                <path fill-rule="evenodd" clip-rule="evenodd" d="M490.92 124.85C487.792 124.85 484.708 125.038 481.678 125.403C469.278 100.843 443.819 84 414.426 84C393.071 84 373.792 92.891 360.089 107.172C347.763 100.564 333.673 96.816 318.708 96.816C270.268 96.816 231 136.084 231 184.524C231 190.136 231.527 195.625 232.534 200.944H567.413C567.197 158.882 533.033 124.85 490.92 124.85Z" fill="#FBFBFB"/>
            </g>
            <defs>
                <filter id="filter0_d_0_1" x="181" y="0" width="436.413" height="216.944" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                    <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                    <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
                    <feOffset dy="-34"/>
                    <feGaussianBlur stdDeviation="25"/>
                    <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.121569 0"/>
                    <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_0_1"/>
                    <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_0_1" result="shape"/>
                </filter>
            </defs>
        </svg>
        <svg style=" position: absolute; top:120%; left:50%" width="618" height="217" viewBox="0 0 618 217" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path fill-rule="evenodd" clip-rule="evenodd" d="M76.493 99.851C79.621 99.851 82.705 100.038 85.735 100.403C98.136 75.843 123.595 59 152.988 59C174.343 59 193.621 67.891 207.324 82.172C219.651 75.564 233.74 71.816 248.705 71.816C297.145 71.816 336.413 111.084 336.413 159.524C336.413 165.136 335.886 170.625 334.879 175.944H0C0.216 133.882 34.38 99.851 76.493 99.851Z" fill="#FDFCFB"/>
            <g filter="url(#filter0_d_0_1)">
                <path fill-rule="evenodd" clip-rule="evenodd" d="M490.92 124.85C487.792 124.85 484.708 125.038 481.678 125.403C469.278 100.843 443.819 84 414.426 84C393.071 84 373.792 92.891 360.089 107.172C347.763 100.564 333.673 96.816 318.708 96.816C270.268 96.816 231 136.084 231 184.524C231 190.136 231.527 195.625 232.534 200.944H567.413C567.197 158.882 533.033 124.85 490.92 124.85Z" fill="#FBFBFB"/>
            </g>
            <defs>
                <filter id="filter0_d_0_1" x="181" y="0" width="436.413" height="216.944" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                    <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                    <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
                    <feOffset dy="-34"/>
                    <feGaussianBlur stdDeviation="25"/>
                    <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.121569 0"/>
                    <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_0_1"/>
                    <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_0_1" result="shape"/>
                </filter>
            </defs>
        </svg>

    </div>

    <%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
    <div class="container" >

        <div class="row">
            <div class="column-left">
                    <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
            </div>

            <div class="column-middle">
                <%@ include file="/WEB-INF/jsp/components/widgets/upperFeedButtons.jsp" %>

                <div id="placeholder-posts-container">
                    <c:forEach begin="1" end="10" var="index">
                        <%@ include file="/WEB-INF/jsp/components/widgets/placeholderBlogpost.jsp" %>
                    </c:forEach>
                </div>
                <div id="actual-posts-container">
                    <c:choose>
                        <c:when test="${empty postList}">
                            <div class="no-posts-found">
                                <i class="circle-icon fa-solid fa-magnifying-glass" style="color:var(--text)"></i>
                                <spring:message code="Posts.notFound"/>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Include the page selector -->
                            <c:if test="${totalPages >  1}">
                                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                                    <jsp:param name="page" value="${page}" />
                                    <jsp:param name="totalPages" value="${totalPages}" />
                                </jsp:include>
                            </c:if>

                            <c:forEach var="post" items="${postList}" >
                                <c:set var="postTags" value="${post.tags}" scope="request"/>
                                <jsp:include page="/WEB-INF/jsp/components/widgets/blogpost.jsp" >
                                    <jsp:param name="postID" value="${post.postId}" />
                                    <jsp:param name="postNeighborMail" value="${post.user.mail}" />
                                    <jsp:param name="postDate" value="${post.date}" />
                                    <jsp:param name="postTitle" value="${post.title}" />
                                    <jsp:param name="postDescription" value="${post.description}" />
                                    <jsp:param name="postImage" value="${post.postPictureId}" />
                                    <jsp:param name="postLikes" value="${post.likes}" />
                                </jsp:include>

                            </c:forEach>


                            <c:if test="${totalPages >  1}">
                                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                                    <jsp:param name="page" value="${page}" />
                                    <jsp:param name="totalPages" value="${totalPages}" />
                                </jsp:include>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
                <script>
                    function showPostsAfterDelay() {
                        document.getElementById('placeholder-posts-container').style.display = 'none';
                        document.getElementById('actual-posts-container').style.display = 'block';
                    }

                    setTimeout(showPostsAfterDelay, 1000);

                </script>
            </div>

            <div class="column-right">

                <%@ include file="/WEB-INF/jsp/components/displays/rightColumn.jsp" %>
            </div>


        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
</div>

</body>

</html>