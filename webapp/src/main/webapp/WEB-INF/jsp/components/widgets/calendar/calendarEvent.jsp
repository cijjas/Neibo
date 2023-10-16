<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="blogpost p-3">
    <div class="row w-100">
        <div class="col-${isAdmin ? '10' : '12'}" style="overflow: hidden; text-overflow: ellipsis;">
            <a href="${pageContext.request.contextPath}/events/${param.eventId}">
                <div><c:out value="${param.eventName}" /></div>
            </a>
        </div>
        <c:if test="${isAdmin}">
            <div class="col-2">
                <span class="admin-link">
                    <a href="${pageContext.request.contextPath}/admin/delete-event/${param.eventId}?timestamp=${selectedTimestamp}" class="btn btn-link">
                        <i class="fas fa-trash" style="color: var(--error);"></i>
                    </a>
                </span>
            </div>
        </c:if>
    </div>
    <a href="${pageContext.request.contextPath}/events/${param.eventId}">
        <p style="font-size: 12px; font-weight: normal">
            <span id="eventStartTime"></span> - <span id="eventEndTime"></span>
        </p>
        <p style="font-size: 12px; font-weight: normal"><c:out value="${param.eventDuration}"/> <spring:message code="Minutes"/></p>
        <div class="divider"></div>
        <div class="postcard-description m-t-40" style="overflow: clip">
            <c:out value="${param.eventDescription}" />
        </div>
    </a>
</div>

<script>
    // JavaScript function to load event start time
    async function loadEventStartTime(eventId) {
        try {
            const response = await fetch("/endpoint/event-start-time?id=" + eventId);
            if (!response.ok) {
                throw new Error("Failed to fetch event start time from the endpoint.");
            }
            const startTimeElement = document.getElementById("eventStartTime");
            startTimeElement.textContent = await response.text();
        } catch (error) {
            console.error(error.message);
        }
    }

    // JavaScript function to load event end time
    async function loadEventEndTime(eventId) {
        try {
            const response = await fetch("/endpoint/eventEndTime?id=" + eventId);
            if (!response.ok) {
                throw Error("Failed to fetch event end time from the endpoint.");
            }
            const endTimeElement = document.getElementById("eventEndTime");
            endTimeElement.textContent = await response.text();
        } catch (error) {
            console.error(error.message);
        }
    }

    // Call the functions with the event ID (replace '123' with the actual value)
    loadEventStartTime(${param.eventId});
    loadEventEndTime(${param.eventId});
</script>
