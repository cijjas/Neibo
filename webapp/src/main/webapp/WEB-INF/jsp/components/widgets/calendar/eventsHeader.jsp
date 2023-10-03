<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="upper-feed-buttons-box">
    <div class="f-c-c-c w-100">
        <h2>
            <spring:message code="date.format">
                <spring:argument value="${selectedDay}"/>
                <spring:argument value="${selectedMonth}"/>
                <spring:argument value="${fn:replace(selectedYear, '.', '')}" />
            </spring:message>
        </h2>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/addEvent" class="cool-feed-button">
                <spring:message code="CreateNewEvent.button" />
                <i class="fa-solid fa-plus"></i>
            </a>
        </c:if>
    </div>

</div>