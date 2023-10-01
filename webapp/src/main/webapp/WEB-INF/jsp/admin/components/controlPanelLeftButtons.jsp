
<div class="left-column-container">
    <div class="left-box" >
        <div class="f-c-c-c">
            <a  href="${pageContext.request.contextPath}/admin/publish" class="cool-left-button ">
                <i class="fas fa-bullhorn m-r-5"></i> <spring:message code="CreateAnnouncement"/>
            </a>
            <div class="divider"></div>
            <a href="${pageContext.request.contextPath}/admin/unverified" class="cool-left-button  ">
                <i class="fa-solid fa-user-check m-r-5"></i> <spring:message code="Requests"/>
            </a>
            <a href="${pageContext.request.contextPath}/admin/neighbors" class="cool-left-button " >
                <i class="fa-solid fa-users m-r-5"></i><spring:message code="NeighborList"/>
            </a>
            <a href="${pageContext.request.contextPath}/admin/amenities" class="cool-left-button " >
                <i class="fas fa-table-tennis-paddle-ball m-r-5"></i> <spring:message code="Amenities"/>
            </a>
            <a id="informationButton" href="${pageContext.request.contextPath}/admin/information" class="cool-left-button " >
                <i class="fas fa-address-card m-r-5"></i> <spring:message code="Information"/>
            </a>

        </div>
        <div class="f-c-c-c">
            <a  href="${pageContext.request.contextPath}/calendar" class="cool-left-button " >
                <i class="fas fa-calendar-days m-r-5"></i> <spring:message code="Calendar"/>
            </a>
        </div>

    </div>
</div>
