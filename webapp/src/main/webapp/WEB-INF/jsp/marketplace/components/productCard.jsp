<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="product-card ">
    <%--information corner--%>
    <div class="inside">
        <div class="icon">
            <i class="fa-solid fa-circle-info"></i>
        </div>
        <div class="contents" style="color: var(--always-background)">
            <c:out value="${param.productDescription}"/>
        </div>
    </div>
        <div class="f-c-c-s g-0 w-100  h-100">
        <div class="header w-100" >
                <c:choose>
                    <c:when test="${param.productUsed}">
                    <div class="used-tag used">
                        <spring:message code="Used"/>
                    </div>
                    </c:when>
                    <c:otherwise>
                        <div class="used-tag new">
                            <spring:message code="New"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            <div class="img-product placeholder-glow">
                <img
                        id="product-image-${param.productId}"
                        src=""
                        class="placeholder"
                        alt="product_image_${param.productId}"
                />
                <script>
                    (function () {
                        getImageInto("product-image-${param.productId}",${empty param.productPrimaryPictureId ? 0 : param.productPrimaryPictureId}, "${pageContext.request.contextPath}")
                    })();

                </script>
            </div>
        </div>
        <div class="footer w-100" >
            <div class="product-title p-2"  style="height: 50%">
                <span>
                    <c:out value="${param.productTitle}"/>
                </span>
            </div>

            <div class="container p-0 " style="height: 50%">
                <div class="f-c-c-c w-100 h-100 g-0">
                    <div class="row w-100" >
                        <div class="col-7  font-weight-bold " >
                            <div class="f-r-c-c g-0">
                                <span class="price font-size-20 ">
                                    <c:out value="${param.productPrice}"/>
                                </span>
                                <div class="f-c-s-c pl-1" style="height: 20px">
                                    <span class="cents c-light-text font-size-12">
                                        <c:out value="${param.productDecimal}"/>
                                    </span>
                                </div>

                            </div>

                        </div>
                        <a href="" class="col-5 cool-button product-button">
                            <spring:message code="See"/>
                            <i class="fa-solid fa-share"></i>
                        </a>
                    </div>
                    <div class="row  pl-2 pr-2" style="max-width: 100%">
                        <a class="tag-option tag">
                            <c:out value="${param.productDepartment}"/>
                        </a>
                    </div>
                </div>

            </div>

        </div>
    </div>

</div>