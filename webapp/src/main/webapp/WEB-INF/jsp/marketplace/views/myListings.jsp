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
    <title><spring:message code="My.listings"/></title>
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

            <div class="w-100 f-c-c-c g-05 cool-static-container">

                <div class="f-c-s-s w-100 ">
                    <div class="f-r-c-c pt-2 pb-2">
                        <a href="${contextPath}/marketplace/my-listings" class="cool-feed-button rounded marketplace-button  font-weight-bold ${channel == "MyListings" ? 'active' : ''}">
                            <span class="font-size-12">
                                <i class="fa-regular fa-chart-bar"></i>
                                <span class="hide-text">
                                    <spring:message code="My.listings"/>
                                </span>
                            </span>
                        </a>
                        <a href="${contextPath}/marketplace/my-sales" class="cool-feed-button rounded marketplace-button font-weight-bold ${channel == "MySales" ? 'active' : ''}">
                        <span class="font-size-12">
                            <i class="fa-solid fa-box-open hide-icons"></i>
                            <span class="hide-text">
                                <spring:message code="My.sales"/>
                            </span>
                        </span>
                        </a>
                    </div>
                </div>
                <div class="divider mb-3"></div>

                <c:choose>
                    <c:when test="${empty myProductList}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                            <spring:message code="No.new.requests"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="product" items="${myProductList}">
                            <div class="cool-static-container w-100 f-c-s-s g-0 p-0 mb-3">

                                <div class="container">
                                    <div class="f-r-c-c w-100 g-0">
                                        <div class="pl-0">
                                            <div class="purchased-product-image f-c-c-c placeholder-glow">
                                                <img
                                                        id="purchased-product-image-${product.productId}"
                                                        src=""
                                                        class="placeholder"
                                                        style="max-height: 100px"
                                                        alt="purchased_product_image_${product.productId}"
                                                />
                                                <script src="${pageContext.request.contextPath}/resources/js/fetchLibrary.js"></script>
                                                <script>
                                                    (function () {
                                                        getImageInto("purchased-product-image-${product.productId}",${empty product.primaryPicture.imageId ? -2 : product.primaryPicture.imageId}, "${pageContext.request.contextPath}")
                                                    })();
                                                </script>
                                            </div>
                                        </div>


                                        <div class="f-c-s-s  w-100 p-4">
                                            <c:choose>
                                                <c:when test="${product.remainingUnits == 0}">
                                                    <div class="f-r-sb-c w-100 g-0">
                                                             <span class="font-weight-bold font-size-16">
                                                                <c:out value="${product.name}"/>
                                                            </span>
                                                        <div class="sold-tag p-2 font-weight-bolder">
                                                            <spring:message code="Sold.out"/>
                                                        </div>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="f-r-sb-c w-100 g-0">
                                                             <span class="font-weight-bold font-size-16">
                                                                <c:out value="${product.name}"/>
                                                            </span>
                                                        <div class="in-stock-tag  p-2 font-weight-bolder">
                                                            <spring:message code="In.stock"/>:
                                                            <c:out value="${product.remainingUnits}"/>
                                                        </div>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>

                                            <div class="f-r-sb-c g-0 w-100">
                                                <div class="f-r-c-c g-0">
                                                       <span class="price font-size-20 font-weight-normal">
                                                            <c:out value="${product.priceIntegerString}"/>
                                                       </span>
                                                    <div class="f-c-s-c pl-1" style="height: 20px">
                                                           <span class="cents c-light-text font-size-12 font-weight-normal">
                                                                <c:out value="${product.priceDecimalString}"/>
                                                           </span>
                                                    </div>
                                                </div>
                                                <div class="f-r-c-c g-05">
                                                    <div class="department-tag" onclick='window.location.href = "${contextPath}/marketplace/products/${product.department.department.departmentUrl}" '>
                                                        <spring:message code="${product.department.department}"/>
                                                    </div>
                                                    <c:choose>
                                                        <c:when test="${product.used}">
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


                                            </div>

                                            <div class="f-r-sb-c w-100">

                                                <div class="f-r-sb-c g-05 w-50">
                                                    <a href="${contextPath}/marketplace/products/${product.department.department.departmentUrl}/${product.productId}" class="cool-button small-a marketplace-button square-radius w-50 font-weight-bold">
                                                        <i class="fa-solid fa-arrow-right-to-bracket pr-1"></i>
                                                        <spring:message code="View.listing"/>
                                                    </a>
                                                    <a href="${contextPath}/marketplace/my-requests/${product.productId}" class="cool-button small-a marketplace-button square-radius w-50 font-weight-bold">
                                                        <i class="fa-solid fa-bell pr-1"></i>
                                                        <spring:message code="Requests"/>
                                                        <span id="request-count-${product.productId}">
                                                            </span>
                                                        <script>
                                                            (function () {
                                                                fetchRequestCount("request-count-${product.productId}", ${product.productId});

                                                                async function fetchRequestCount(elementId, productId) {
                                                                    try {
                                                                        const response = await fetch("${pageContext.request.contextPath}/endpoint/request-count/" + productId);
                                                                        if (response.ok) {
                                                                            const requestCount = await response.text();
                                                                            document.getElementById(elementId).innerHTML ="(" + requestCount + ")";
                                                                        }

                                                                    } catch (error) {
                                                                        console.error(error.message);
                                                                    }
                                                                }
                                                            })();

                                                        </script>
                                                    </a>
                                                </div>
                                            </div>

                                        </div>
                                    </div>
                                </div>
                            </div>

                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <c:if test="${totalPages >  1}">
                    <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                        <jsp:param name="page" value="${page}"/>
                        <jsp:param name="totalPages" value="${totalPages}"/>
                    </jsp:include>
                </c:if>

            </div>
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
