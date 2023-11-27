<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
    <title><spring:message code="My.requests"/> - <c:out value="${product.name}"/></title>
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
            <div class="w-100 f-c-c-c g-1 cool-static-container">
                <div class="f-r-s-c w-100">
                    <a href="${contextPath}/marketplace/my-listings" class="marketplace-back">
                        <i class="fa-solid fa-arrow-left"></i>
                    </a>
                </div>
                <div class="f-c-c-c">

                    <div class="f-r-c-c w-100 pt-1 pb-1">
                        <h1 class="font-weight-bolder font-size-24">
                            <c:out value="${product.name}"/>
                        </h1>
                    </div>
                    <div class="f-r-c-c g-0">
                       <span class="price font-size-20 font-weight-normal ">
                            <c:out value="${product.priceIntegerString}"/>
                       </span>
                        <div class="f-c-s-c pl-1" style="height: 20px">
                           <span class="cents c-light-text font-size-12 font-weight-normal">
                                <c:out value="${product.priceDecimalString}"/>
                           </span>
                        </div>
                    </div>
                </div>
                <div class="divider"></div>
                <div class="f-r-c-c w-100 pt-2 pb-2">
                    <h1 class="font-weight-normal font-size-16">
                        <spring:message code="List.of.interested.people"/>
                    </h1>
                </div>

                <c:choose>
                    <c:when test="${empty requestList}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                            <spring:message code="No.new.requests"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="request" items="${requestList}" varStatus="loop">
                            <div class="cool-static-container">
                                <div class="f-r-sb-c w-100">
                                    <div class="f-r-c-c">
                                        <div class="f-c-s-s">

                                            <div>
                                                <spring:message code="Requester"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.user.name}"/>
                                                </span>
                                            </div>
                                            <div>
                                                <spring:message code="Email"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.user.mail}"/>
                                                </span>
                                            </div>
                                            <div>
                                                <spring:message code="PhoneNumber"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.user.phoneNumber}"/>
                                                </span>
                                            </div>
                                            <div>
                                                <spring:message code="Request.date"/>:
                                                <span style="color: var(--lila)">
                                                    <fmt:formatDate value="${request.requestDate}" pattern="dd MMM yyyy" var="formattedDate" />
                                                    <fmt:formatDate value="${request.requestDate}" pattern="HH:mm" var="formattedTime" />

                                                    <c:out value="${formattedDate}" />
                                                    -
                                                    <c:out value="${formattedTime}" />
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    <a onclick="openMarkAsSoldDialog(${request.user.userId}, ${request.requestId})" class="cool-button small-a marketplace-button  font-weight-bold ">
                                        <spring:message code="Mark.as.sold"/>
                                    </a>
                                </div>
                                <div class="divider mt-2 mb-2"></div>
                                <div class="f-r-s-s w-100">
                                    <div class="f-r-c-c">
                                        <div class="f-c-s-s">
                                            <div>
                                                <spring:message code="Message"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.message}"/>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${totalPages >  1}">
                                    <jsp:include page="/WEB-INF/jsp/components/widgets/pageSelector.jsp">
                                        <jsp:param name="page" value="${page}"/>
                                        <jsp:param name="totalPages" value="${totalPages}"/>
                                    </jsp:include>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
    function openMarkAsSoldDialog(buyerId, requestId) {
        const idT = document.getElementById('id-t');
        idT.innerHTML = buyerId;
        const reqT = document.getElementById('req-t');
        reqT.innerHTML = requestId;
        document.getElementById('mark-as-sold-dialog').style.display = 'flex';
    }

    function closeMarkAsSoldDialog() {
        document.getElementById('mark-as-sold-dialog').style.display = 'none';
    }

    function submitMarkAsSold() {
        document.getElementById('loader-container').style.display = 'flex';
        document.getElementById('buyer-id').value = document.getElementById('id-t').innerHTML;
        document.getElementById('request-id').value = document.getElementById('req-t').innerHTML;
        const form = document.forms['markAsSoldForm'];
        form.submit();
    }
</script>

<div id="mark-as-sold-dialog" class="dialog" style="display: none">
    <div class="dialog-content marketplace" >
        <div class="dialog-header">
            <div class="dialog-svg">
                <svg width="242" height="219" viewBox="0 0 242 219" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <g filter="url(#filter0_d_17_17)">
                        <path d="M26.8122 56.1162C28.0135 49.5985 34.2709 45.2888 40.7885 46.49L50.0518 48.1973L57.7951 49.6245L207.734 77.2595L214.773 78.5569L225.199 80.4785C231.716 81.6797 236.026 87.9371 234.825 94.4548L215.187 201C213.986 207.518 207.729 211.828 201.211 210.626L16.8011 176.638C10.2834 175.437 5.97367 169.179 7.17493 162.662L26.8122 56.1162Z" fill="#7D7AE3"></path>
                        <path d="M137.333 24.1789L43.5706 58.7884C43.226 58.9156 42.8521 58.9415 42.4931 58.8632C40.6084 58.4516 40.4326 55.8323 42.2454 55.1726L137.39 20.5474C140.028 19.5873 142.977 20.0805 145.159 21.847L213.114 76.8545C214.411 77.9039 213.669 80 212.001 80C211.59 80 211.192 79.8573 210.876 79.5964L145.188 25.5082C142.992 23.7 140.001 23.1939 137.333 24.1789Z" fill="#7D7AE3"></path>
                        <circle cx="142.151" cy="13.1512" r="7" stroke="#7D7AE3" stroke-width="4.30235"></circle>
                    </g>
                    <defs>
                        <filter id="filter0_d_17_17" x="0.973633" y="0" width="240.052" height="218.827" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                            <feFlood flood-opacity="0" result="BackgroundImageFix"></feFlood>
                            <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"></feColorMatrix>
                            <feOffset dy="2"></feOffset>
                            <feGaussianBlur stdDeviation="3"></feGaussianBlur>
                            <feComposite in2="hardAlpha" operator="out"></feComposite>
                            <feColorMatrix type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"></feColorMatrix>
                            <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_17_17"></feBlend>
                            <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_17_17" result="shape"></feBlend>
                        </filter>
                    </defs>
                </svg>
            </div>
            <div class="boxy-poxy p-2" id="boxy-poxy">
                <div style="position: relative; color:var(--always-background)" class="f-c-s-s g-05">
                    <h3 class="font-size-20 font-weight-bold" ><spring:message code="Confirm.sale"/></h3>
                    <p style="text-align: start" class="font-weight-normal">
                        <spring:message code="Sold.message"/>
                    </p>
                </div>
            </div>
            <a class="close-button marketplace" onclick="closeMarkAsSoldDialog()">
                <i class="fas fa-close"></i>
            </a>
        </div>
        <span id="id-t" hidden="hidden"></span>
        <span id="req-t" hidden="hidden"></span>

        <form:form class="f-c-c-c w-100" id="markAsSoldForm" name="markAsSoldForm" method="post" action="${contextPath}/marketplace/my-requests/${product.productId}" modelAttribute="markAsSoldForm" enctype="multipart/form-data">
            <div class="f-c-c-c w-100  g-0">
                <form:hidden id="buyer-id" path="buyerId" value=""/>
                <form:hidden id="request-id" path="requestId" value=""/>

                <div class="f-r-c-c w-100 font-size-16 font-weight-normal g-05">
                    <span class="c-text"><spring:message code="Quantity"/></span>

                    <div class="">
                        <label for="condition"></label>
                        <form:select path="quantity"  class="cool-input marketplace-input quantity-input font-weight-bold font-size-14" name="condition" id="condition">
                            <c:forEach begin="1" end="${product.remainingUnits}" varStatus="loop">
                                <form:option value="${loop.index}">${loop.index}</form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>
                <form:errors path="quantity" cssClass="error pt-1" element="p"/>
            </div>
            <button type="submit" onclick="submitMarkAsSold()" class=" w-75 cool-button marketplace-button pure filled-interesting square-radius font-size-14 font-weight-bold">
                <spring:message code="Confirm"/>
            </button>
        </form:form>
    </div>
</div>


<div id="loader-container" class="loader-container ">
    <div class="cool-static-container small-size-container">
        <div style="font-weight: bold; font-size: 16px"><spring:message code="Sending.your.message"/>...</div>
        <div class="loader marketplace" style="margin-top: 20px"></div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-HwwvtgBNo3bZJJLYd8oVXjrBZt8cqVSpeBNS5n7C8IVInixGAoxmnlMuBnhbgrkm"
        crossorigin="anonymous"></script>
<%@ include file="/WEB-INF/jsp/components/displays/footer.jsp" %>
<script>
    const wave3 = document.getElementById('wave3');
    const wave2 = document.getElementById('wave2');
    wave2.classList.add('lila');
    wave3.classList.add('lila');

</script>
</body>
</html>
