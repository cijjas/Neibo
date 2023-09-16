<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html lang="en">

<%@ include file="/WEB-INF/jsp/components/head.jsp" %>


<body class="body">
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>
    <div class="container " >



        <div class="row">
            <div class="column column-left">
                <%@ include file="/WEB-INF/jsp/components/leftcolumn.jsp" %>
            </div>

            <div class="column column-middle">
                <script>
                    // JavaScript function to toggle button selection
                    function toggleButtonSelection(buttonId) {
                        const buttons = document.querySelectorAll('.filter-button');

                        buttons.forEach(function (button) {
                            if (button.id === buttonId) {
                                button.classList.add('selected');
                            } else {
                                button.classList.remove('selected');
                            }
                        });
                    }
                </script>


                <%@ include file="/WEB-INF/jsp/components/upperFeedButtons.jsp" %>

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

                <!-- Include the page selector -->
                <jsp:include page="/WEB-INF/jsp/components/pageselector.jsp">
                    <jsp:param name="page" value="${page}" />
                    <jsp:param name="totalPages" value="${totalPages}" />
                </jsp:include>

            </div>

            <div class="column column-right">
                <%@ include file="/WEB-INF/jsp/components/rightcolumn.jsp" %>
            </div>


        </div>
    </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>

</body>
</html>