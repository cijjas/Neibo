<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="upper-feed-buttons-box w-100 ">
    <div class="f-r-c-c">

        <div style="width: 200px" ${channel == "Marketplace" ? '' : 'hidden="hidden"'}>
            <label for="department"></label>
            <select class="cool-input marketplace-input font-weight-bold font-size-12"  name="department" id="department">
                <option value="ELECTRONICS"><spring:message code="ELECTRONICS"/></option>
                <option value="APPLIANCES"><spring:message code="APPLIANCES"/></option>
                <option value="HOME_FURNITURE"><spring:message code="HOME_FURNITURE"/></option>
                <option value="CLOTHING_FASHION"><spring:message code="CLOTHING_FASHION"/></option>
                <option value="HEALTH_BEAUTY"><spring:message code="HEALTH_BEAUTY"/></option>
                <option value="SPORTS_OUTDOORS"><spring:message code="SPORTS_OUTDOORS"/></option>
                <option value="BOOKS_MEDIA"><spring:message code="BOOKS_MEDIA"/></option>
                <option value="TOYS_GAMES"><spring:message code="TOYS_GAMES"/></option>
                <option value="JEWELRY_ACCESSORIES"><spring:message code="JEWELRY_ACCESSORIES"/></option>
                <option value="AUTOMOTIVE"><spring:message code="AUTOMOTIVE"/></option>
                <option value="GROCERIES_FOOD"><spring:message code="GROCERIES_FOOD"/></option>
                <option value="PETS_SUPPLIES"><spring:message code="PETS_SUPPLIES"/></option>
                <option value="HOME_IMPROVEMENT"><spring:message code="HOME_IMPROVEMENT"/></option>
                <option value="GARDEN_OUTDOOR"><spring:message code="GARDEN_OUTDOOR"/></option>
                <option value="OFFICE_SUPPLIES"><spring:message code="OFFICE_SUPPLIES"/></option>
                <option value="BABY_KIDS"><spring:message code="BABY_KIDS"/></option>
                <option value="ARTS_CRAFTS"><spring:message code="ARTS_CRAFTS"/></option>
                <option value="TRAVEL_LUGGAGE"><spring:message code="TRAVEL_LUGGAGE"/></option>
                <option value="MUSIC_INSTRUMENTS"><spring:message code="MUSIC_INSTRUMENTS"/></option>
                <option value="TECHNOLOGY"><spring:message code="TECHNOLOGY"/></option>
            </select>
        </div>


        <div class="f-r-s-c filters-marketplace g-05">
            <a href="${contextPath}/marketplace/products" class="cool-feed-button rounded marketplace-button ${channel == "Marketplace" ? 'active' : ''}">
                <span class="font-size-12">
                    <i class="fas fa-shopping-cart "></i>
                    <span class="hide-text">
                        <spring:message code="Marketplace"/>
                    </span>
                </span>
            </a>

            <a href="${contextPath}/marketplace/my-purchases" class="cool-feed-button rounded marketplace-button ${channel == "MyPurchases" ? 'active' : ''}">
                <span class="font-size-12">
                    <i class="fa-solid fa-bag-shopping hide-icons"></i>
                    <span class="hide-text">
                        <spring:message code="My.purchases"/>
                    </span>
                </span>
            </a>

            <div class="f-r-s-c sub-filters g-05">
                <a href="${contextPath}/marketplace/my-sales" class="cool-feed-button rounded marketplace-button ${channel == "MySales" ? 'active' : ''}">
                    <span class="font-size-12">
                        <i class="fa-solid fa-tags hide-icons"></i>
                        <span class="hide-text">
                            <spring:message code="My.sales"/>
                        </span>
                    </span>
                </a>

                <a href="${contextPath}/marketplace/my-listings" class="cool-feed-button rounded marketplace-button ${channel == "MyListings" ? 'active' : ''}">
                    <span class="font-size-12">
                        <i class="fa-solid fa-box-open hide-icons"></i>
                        <span class="hide-text">
                            <spring:message code="My.listings"/>
                        </span>
                    </span>
                </a>
            </div>
        </div>
    </div>

    <a href="${contextPath}/marketplace/create-listing" class="cool-feed-button marketplace-button square font-size-16 font-weight-bold ${channel == "Sell" ? 'active' : ''}">
        <i class="fa-solid fa-plus"></i>
        <spring:message code="Sell"/>
    </a>
</div>