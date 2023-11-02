<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<div class="upper-feed-buttons-box w-100 ">
    <div class="f-r-c-c">

       <c:if test="${channel == 'Marketplace'}">
           <div style="width: 200px" >
               <label for="department"></label>
               <select class="cool-input marketplace-input font-weight-bold font-size-12" name="department" id="department" onchange="redirectToDepartment()">
                   <c:forEach items="${departmentList}" var="department">
                       <option value="${department.key}" data-string="${department.value}"><spring:message code="${department.value}" /></option>
                   </c:forEach>
               </select>
               <script>
                   window.addEventListener('DOMContentLoaded', function () {
                       const departmentValue = ${empty param.department? 22 : param.department};
                       const select = document.getElementById("department");
                       for (const option of select.options) {
                           if (parseInt(option.value) === departmentValue) {
                               option.selected = true;
                           }
                       }
                   });
                   function redirectToDepartment() {
                       const select = document.getElementById("department");
                       const selectedValue = select.options[select.selectedIndex].value; // Use 'value' instead of 'text'
                       window.location.href = "${contextPath}/marketplace/products?department=" + selectedValue;
                   }
               </script>

           </div>
       </c:if>



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