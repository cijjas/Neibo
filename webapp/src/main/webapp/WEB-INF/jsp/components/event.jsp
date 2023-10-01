<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class ="card-container">
    <div class="post-section">
        <div class="post-info">
            <div class="row">
                <div class="col-md-8">
                    <h2><c:out value="${param.eventName}" /></h2>
                </div>

                <div class="col-md-4 text-right">
                    <a href="${pageContext.request.contextPath}/admin/deleteEvent/${param.eventId}?timestamp=${selectedTimestamp}" class="btn btn-link">
                        <i class="fas fa-trash" style="color: var(--error);"></i>
                    </a>
                </div>
            </div>
            <p style="font-size: 12px; font-weight: normal"><c:out value="${param.eventDuration}"></c:out> <spring:message code="Minutes"/></p>
            <div class="divider"></div>
            <div class="postcard-description">
                <c:out value="${param.eventDescription}" />
            </div>
        </div >
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/event.js"></script>

