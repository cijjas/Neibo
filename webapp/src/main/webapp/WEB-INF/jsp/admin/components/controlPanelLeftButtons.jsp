
<div class="left-column-container">
    <div class="left-box" >
        <div class="f-c-c-c">
            <a  href="${pageContext.request.contextPath}/admin/publish" class="cool-left-button  ">
                <i class="fas fa-bullhorn"></i> <spring:message code="CreateAnnouncement"/>
            </a>
            <a  href="${pageContext.request.contextPath}/admin/unverified" class="cool-left-button  ">
                <i class="fas fa-circle-check"></i> <spring:message code="Requests"/>
            </a>
            <a  href="${pageContext.request.contextPath}/admin/calendar" class="cool-left-button " >
                <i class="fas fa-calendar-days"></i> <spring:message code="Events"/>
            </a>
            <a  href="${pageContext.request.contextPath}/admin/amenities" class="cool-left-button ${channel == 'Reservations' ? 'active' : ''}" >
                <i class="fas fa-table-tennis-paddle-ball"></i> <spring:message code="Amenities"/>
            </a>
            <a id="informationButton" href="${pageContext.request.contextPath}/admin/information" class="cool-left-button ${channel == 'Information' ? 'active' : ''}" >
                <i class="fas fa-address-card"></i> <spring:message code="Information"/>
            </a>
            <a href="${pageContext.request.contextPath}/admin/neighbors" class="cool-left-button " >
                <i class="fas fa-envelope"></i> <spring:message code="NeighborList"/>
            </a>
        </div>

    </div>
</div>
