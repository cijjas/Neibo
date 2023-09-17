<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>

<html>
<%@ include file="/WEB-INF/jsp/components/head.jsp" %>

<link href="${pageContext.request.contextPath}/resources/css/postcard.css" rel="stylesheet"/>
<body>
<!-- Navigation Bar -->
    <%@ include file="/WEB-INF/jsp/components/navbar.jsp" %>

    <div class="container ">
        <div class="row">
            <div class="column column-left">
                <%@ include file="/WEB-INF/jsp/components/leftColumn.jsp" %>
            </div>

            <div class="column column-middle ">
                <%@ include file="/WEB-INF/jsp/components/postcard.jsp" %>
            </div>

            <div class="column column-right">
                <%@ include file="/WEB-INF/jsp/components/rightColumn.jsp" %>
            </div>
        </div>
    </div>


    <!-- Bootstrap JS and jQuery -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm" crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/footer.jsp" %>

</body>
</html>
