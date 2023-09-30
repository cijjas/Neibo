<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class ="card-container">
    <div class="post-section">
        <div class="post-info">
            <h2><c:out value="${param.eventName}" /></h2>
            <p style="font-size: 12px; font-weight: normal"><c:out value="${param.eventDuration}"></c:out> <spring:message code="Minutes"/></p>
            <div class="divider"></div>
            <div class="postcard-description">
                <c:out value="${param.eventDescription}" />
            </div>
        </div >
    </div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/event.js"></script>

