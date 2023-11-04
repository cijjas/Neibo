<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="w-100" style="position: relative; z-index: 2" >
    <div class="upper-feed-buttons-box w-100"  >
        <div class="f-r-c-c  g-05">
            <div class="f-r-s-c filters-marketplace g-05">
                <a href="${contextPath}/marketplace/products" class="cool-feed-button rounded marketplace-button ${channel == "Marketplace" ? 'active' : ''}">
                    <span class="font-size-12">
                        <i class="fas fa-shopping-cart "></i>
                        <span class="hide-text">
                            <spring:message code="Marketplace"/>
                        </span>
                    </span>
                </a>

                <div class="f-r-s-c sub-filters g-05">
                    <a href="${contextPath}/marketplace/my-purchases" class="cool-feed-button rounded marketplace-button ${channel == "MyPurchases" ? 'active' : ''}">
                    <span class="font-size-12">
                        <i class="fa-solid fa-bag-shopping hide-icons"></i>
                        <span class="hide-text">
                            <spring:message code="My.purchases"/>
                        </span>
                    </span>
                    </a>
                    <a href="${contextPath}/marketplace/my-sales" class="cool-feed-button rounded marketplace-button ${channel == "MySales" ? 'active' : ''}">
                        <span class="font-size-12">
                            <i class="fa-solid fa-box-open hide-icons"></i>
                            <span class="hide-text">
                                <spring:message code="My.sales"/>
                            </span>
                        </span>
                    </a>

                    <a href="${contextPath}/marketplace/my-listings" class="cool-feed-button rounded marketplace-button ${channel == "MyListings" ? 'active' : ''}">
                        <span class="font-size-12">
                            <i class="fa-solid fa-bars-staggered"></i>
                            <span class="hide-text">
                                <spring:message code="My.listings"/>
                            </span>
                        </span>
                    </a>
                </div>
            </div>
            <c:if test="${channel == 'Marketplace'}">
                <div  >
                    <div class="custom-dropdown" >
                        <div class="dropdown-toggle"><spring:message code="Categories"/></div>
                        <ul class="dropdown-options">
                            <li data-value="0"><a href="${contextPath}/marketplace/products?department=22"><spring:message code="All"/></a></li>
                            <c:forEach items="${departmentList}" var="department">
                                <c:if test="${department.value != 'NONE'}">
                                    <li>
                                        <a href="${contextPath}/marketplace/products?department=${department.key}"><spring:message code="${department.value}" /></a>
                                    </li>
                                </c:if>

                            </c:forEach>
                        </ul>
                    </div>

                </div>
            </c:if>
        </div>


        <a href="${contextPath}/marketplace/create-listing" class="cool-feed-button p-3  marketplace-button square font-size-16 font-weight-bold ${channel == "Sell" ? 'active' : ''}">
            <i class="fa-solid fa-tags"></i>
            <spring:message code="Sell"/>
        </a>
    </div>
</div>
