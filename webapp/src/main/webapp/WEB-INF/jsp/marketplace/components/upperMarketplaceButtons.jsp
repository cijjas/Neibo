<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="upper-feed-buttons-box w-100 ">
    <div class="f-r-s-c filters-marketplace g-05" >
        <a href="${contextPath}/marketplace/products" class="cool-feed-button rounded  marketplace-button ${ channel == "Marketplace" ? 'active' : ''}">
            <i class="fas fa-shopping-cart"></i>
            <spring:message code="Marketplace"/>
        </a>

        <a href="${contextPath}/marketplace/my-purchases" class="cool-feed-button rounded marketplace-button ${channel == "MyPurchases" ? 'active' : ''}">
            <i class="fa-solid fa-bag-shopping"></i>
            <spring:message code="My.purchases"/>
        </a>

        <div class="f-r-s-c sub-filters g-05">

            <a href="${contextPath}/marketplace/my-sales" class="cool-feed-button rounded  marketplace-button ${ channel == "MySales" ? 'active' : ''}">
                <i class="fa-solid fa-tags"></i>
                <spring:message code="My.sales"/>
            </a>
            <a href="${contextPath}/marketplace/my-listings" class="cool-feed-button rounded  marketplace-button ${ channel == "MyListings" ? 'active' : ''}">
                <i class="fa-solid fa-box-open"></i>
                <spring:message code="My.listings"/>
            </a>
        </div>

    </div>
    <a href="${contextPath}/marketplace/create-listing" class="cool-feed-button marketplace-button ${ channel == "Sell" ? 'active' : ''}">
        <i class="fa-solid fa-hand-holding-dollar"></i>
        <spring:message code="Sell"/>
    </a>
</div>