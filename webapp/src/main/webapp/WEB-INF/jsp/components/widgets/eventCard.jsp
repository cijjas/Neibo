<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="card-container">
    <div class="post-section">
        <div class="post-info">
            <h2><c:out value="${event.name}" /></h2>
            <p style="font-size: 12px; font-weight: normal"><c:out value="${event.description}" /></p>
            <div class="divider"></div>
            <div class="postcard-description">
                <spring:message code="Date"/>: <c:out value="${event.date}" />
            <br>
                <span id="eventStartTime"></span> - <span id="eventEndTime"></span>
            </div>
            <div class="divider"></div>
            <div class="postcard-description">

                <spring:message code="Duration"/>: <c:out value="${event.duration}" /> <spring:message code="Minutes"/>
            </div>
        </div >

<%--        Button for attending--%>
        <c:choose>
            <c:when test="${willAttend == false}">
                <div class="d-flex flex-column justify-content-center align-items-end">
                    <form method="post" action="${pageContext.request.contextPath}/attend/${event.eventId}">
                        <div class="d-flex flex-row justify-content-end align-items-center">
                            <div class="d-flex flex-column justify-content-center align-items-end">
                                <button id="submitAttendance" type="submit" class="accept-button outlined" style="margin-top:5px; font-size: 12px; font-weight: bold;">
                                    <spring:message code="Attend"/>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <div class="d-flex flex-column justify-content-center align-items-end">
                    <form method="post" action="${pageContext.request.contextPath}/unattend/${event.eventId}">
                        <div class="d-flex flex-row justify-content-end align-items-center">
                            <div class="d-flex flex-column justify-content-center align-items-end">
                                <button id="submitUnattendance" type="submit" class="ignore-button outlined" style="margin-top:5px; font-size: 12px;">
                                    <spring:message code="Unattend"/>
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%--    List of attendees:--%>
    <div class="post-section">
        <div class="post-info">
            <h3 style="text-align: center;"><spring:message code="Attendees"/></h3>
            <div class="divider"></div>
            <c:choose>
                <c:when test="${empty attendees}">
                    <div class="postcard-description">
                        <spring:message code="NoAttendees"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${attendees}" var="attendee">
                        <div class="postcard-description">
                            <c:out value="${attendee.name} " /><c:out value="${attendee.surname}" />
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

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
    loadEventStartTime(${event.eventId});
    loadEventEndTime(${event.eventId});
</script>