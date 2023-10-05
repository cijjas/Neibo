<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="blogpost">
        <div class="f-r-sb-c">
            <a href="${pageContext.request.contextPath}/events/${param.eventId}">

                <div><c:out value="${param.eventName}"/></div>
            </a>
            <span class="w-100 ">

            </span>
            <c:if test="${isAdmin}">
                <span class="admin-link">
                    <a href="${pageContext.request.contextPath}/admin/deleteEvent/${param.eventId}?timestamp=${selectedTimestamp}" class="btn btn-link">
                        <i class="fas fa-trash" style="color: var(--error);"></i>
                    </a>
                </span>
            </c:if>
        </div>
    <a href="${pageContext.request.contextPath}/events/${param.eventId}">

        <p style="font-size: 12px; font-weight: normal"><c:out value="${param.eventDuration}"/> <spring:message code="Minutes"/></p>
        <div class="divider "></div>
        <div class="postcard-description m-t-40">
            <c:out value="${param.eventDescription}" />
        </div>
    </a>
</div>
