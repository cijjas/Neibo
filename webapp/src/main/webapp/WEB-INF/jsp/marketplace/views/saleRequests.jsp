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


            <div class="w-100 f-c-c-c g-1 cool-static-container">
                <div class="f-c-c-c">
                    <div class="f-r-c-c w-100 pt-2 pb-2">
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

                <div class="f-r-c-c w-100 pt-2 pb-2">
                    <h1 class="font-weight-normal font-size-16">
                        <spring:message code="List.of.interested.people"/>
                    </h1>
                </div>

                <c:choose>
                    <c:when test="${empty requests}">
                        <div class="no-posts-found">
                            <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                            <spring:message code="No.new.requests"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="request" items="${requests}" varStatus="loop">
                            <div class="cool-static-container">
                                <div class="f-r-sb-c w-100">
                                    <div class="f-r-c-c">
                                        <div class="f-c-s-s">
                                            <div>
                                                <spring:message code="Request.date"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.creationDate}"/>
                                                </span>
                                            </div>
                                            <div>
                                                <spring:message code="Requester"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.name}"/>
                                                </span>
                                            </div>
                                            <div>
                                                <spring:message code="Email"/>:
                                                <span style="color: var(--lila)">
                                                    <c:out value="${request.mail}"/>
                                                </span>
                                            </div>
                                        </div>

                                    </div>
                                    <a onclick="handleUserTransaction(${request.userId}, ${product.productId})" class="cool-button small-a marketplace-button  font-weight-bold ">
                                        <spring:message code="Mark.as.sold"/>
                                    </a>
                                </div>

                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

            </div>


        </div>
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

    function handleUserTransaction(buyerId, productId) {
        const form = document.createElement('form');
        form.method = 'POST';

        form.action = '${pageContext.request.contextPath}/marketplace/mark-as-bought';

        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'buyerId';
        input.value = buyerId;
        form.appendChild(input);

        const input2 = document.createElement('input');
        input2.type = 'hidden';
        input2.name = 'productId';
        input2.value = productId;
        form.appendChild(input2);

        document.body.appendChild(form);
        form.submit();
    }

</script>
</body>
</html>
