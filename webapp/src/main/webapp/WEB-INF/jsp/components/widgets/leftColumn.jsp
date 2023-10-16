<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<div class="left-column-container">

    <div class="f-c-c-c sticky" >


        <div class="left-box " >

            <div class="f-c-c-c">
                <c:if test='${loggedUser.role.toString() == "ADMINISTRATOR"}'>
                    <a href="${pageContext.request.contextPath}/admin/publish">
                        <div class="cool-left-button admin-button f-r-sb-c" style="width: 200px; ">
                            <i class="fa-solid fa-chart-pie"></i>
                            Admin panel
                        </div>
                    </a>
                    <div class="    divider"></div>
                </c:if>
                <a id="announcementsButton" href="${pageContext.request.contextPath}/announcements" class="cool-left-button  ${channel == 'Announcements' ? 'active' : ''} f-r-sb-c">
                    <i class="fas fa-bullhorn"></i>
                    <spring:message code="Announcements"/>
                </a>
                <a id="feedButton" href="${pageContext.request.contextPath}/" class="cool-left-button  ${channel == 'Feed' ? 'active' : ''} f-r-sb-c">
                    <i class="fas fa-comments"></i> <spring:message code="Feed"/>
                </a>
                <a id="complaintsButton" href="${pageContext.request.contextPath}/complaints" class="cool-left-button ${channel == 'Complaints' ? 'active' : ''} f-r-sb-c" >
                    <i class="fas fa-envelope"></i> <spring:message code="Complaints"/>
                </a>
            </div>

            <div class="f-c-c-c">
                <div class="divider"></div>
                <a id="servicesButton" href="${pageContext.request.contextPath}/services" class="cool-left-button f-r-sb-c" >
                    <i class="fa-solid fa-handshake-angle"></i> <spring:message code="Services"/>
                </a>
                <a id="reservationsButton" href="${pageContext.request.contextPath}/amenities" class="cool-left-button ${channel == 'Reservations' ? 'active' : ''} f-r-sb-c" >
                    <i class="fas fa-calendar-days"></i> <spring:message code="Reservations"/>
                </a>
                <a id="informationButton" href="${pageContext.request.contextPath}/information" class="cool-left-button ${channel == 'Information' ? 'active' : ''} f-r-sb-c" >
                    <i class="fa-solid fa-circle-info"></i><spring:message code="Information"/>
                </a>

            </div>

        </div>
    </div>

</div>


