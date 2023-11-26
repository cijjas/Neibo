<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Lexend+Deca&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/home.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/commons.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/resources/css/calendarWidget.css" rel="stylesheet"/>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/images/logo.ico">
    <title><spring:message code="My.purchases"/></title>
</head>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page" />
<c:set var="channel" value="${channel}" scope="page" />
<body class="${loggedUser.darkMode ? 'dark-mode' : ''}">
<%@ include file="/WEB-INF/jsp/components/displays/navbar.jsp" %>
<div class="container">
    <div class="row init ">
        <div class="column-left " >
            <%@ include file="/WEB-INF/jsp/components/widgets/leftColumn.jsp" %>
        </div>
        <div id="center-grid" class="column-center pl-3 ">
            <%@ include file="/WEB-INF/jsp/marketplace/components/upperMarketplaceButtons.jsp" %>

            <c:if test="${totalPages >  1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}"/>
                    <jsp:param name="totalPages" value="${totalPages}"/>
                </jsp:include>
            </c:if>

            <div class="cool-static-container w-100">
                <div class="f-c-c-c w-100 mb-3">
                    <div class="f-r-c-c w-50 pt-2 pb-2">
                        <a href="${contextPath}/marketplace/my-purchases" class="cool-button small-a marketplace-button w-50 font-weight-bold ${channel == "MyPurchases" ? 'active' : ''}">
                        <span class="font-size-12">
                            <i class="fa-solid fa-calendar-check"></i>
                            <span class="hide-text">
                                <spring:message code="My.purchases"/>
                            </span>
                        </span>
                        </a>
                        <a href="${contextPath}/marketplace/currently-requesting" class="cool-button small-a marketplace-button w-50 font-weight-bold ${channel == "CurrentlyRequesting" ? 'active' : ''}">
                        <span class="font-size-12">
                            <i class="fa-solid fa-basket-shopping"></i>
                            <span class="hide-text">
                                <spring:message code="My.requests"/>
                            </span>
                        </span>
                        </a>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty purchases}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                            <spring:message code="Purchases.not.found"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="w-100 f-c-c-c g-1 ">
                            <c:forEach var="purchase" items="${purchases}">
                                <div class="cool-static-container w-100 f-c-s-s g-0 p-0">
                                    <div class="p-2 pl-3">
                                        <c:out value="${purchase.purchaseDate}"/>
                                    </div>

                                    <div class="divider m-0"></div>
                                    <div class="container">
                                        <div class="f-r-c-c w-100 g-1">
                                            <div class="pl-0">
                                                <div class="purchased-product-image f-c-c-c placeholder-glow">
                                                    <img
                                                            id="purchased-product-image-${purchase.product.productId}"
                                                            src=""
                                                            class="placeholder"
                                                            alt="purchased_product_image_${purchase.product.productId}"
                                                    />
                                                    <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>
                                                    <script>
                                                        (function () {
                                                            getImageInto("purchased-product-image-${purchase.product.productId}",${empty purchase.product.primaryPicture.imageId ? -2 : purchase.product.primaryPicture.imageId}, "${pageContext.request.contextPath}")
                                                        })();
                                                    </script>
                                                </div>
                                            </div>

                                            <div class="f-c-s-s g-05 w-100 p-4">
                                                <div class="f-r-sb-c g-0 w-100">

                                                    <span class="font-weight-bold font-size-16">
                                                        <c:out value="${purchase.product.name}" />
                                                    </span>
                                                    <c:choose>
                                                        <c:when test="${purchase.product.used}">
                                                            <div class="used-tag used font-weight-normal">
                                                                <spring:message code="Used"/>
                                                            </div>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="used-tag new font-weight-normal">
                                                                <spring:message code="New"/>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </div>
                                                <div class="f-r-c-c font-weight-normal ">
                                                    <spring:message code="Quantity"/>:
                                                    <c:out value="${purchase.units}"/>
                                                </div>
                                                <div class="f-r-c-c g-05" >
                                                   <span class="font-weight-normal">
                                                    <spring:message code="Sold.by"/>
                                                   </span>
                                                    <span style="color: var(--lila)">
                                                       <c:out value="${purchase.product.seller.name}"/>
                                                   </span>
                                                </div>

                                                <div class="f-r-c-c g-0 pt-2">
                                                   <span class="price font-size-20 font-weight-normal ">
                                                        <c:out value="${purchase.product.priceIntegerString}"/>
                                                   </span>
                                                    <div class="f-c-s-c pl-1" style="height: 20px">
                                                       <span class="cents c-light-text font-size-12 font-weight-normal">
                                                            <c:out value="${purchase.product.priceDecimalString}"/>
                                                       </span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>


            <c:if test="${totalPages >  1}">
                <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                    <jsp:param name="page" value="${page}"/>
                    <jsp:param name="totalPages" value="${totalPages}"/>
                </jsp:include>
            </c:if>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
<script>
    const wave3= document.getElementById('wave3');
    const wave2= document.getElementById('wave2');
    wave2.classList.add('lila');
    wave3.classList.add('lila');
</script>
</body>
</html>
