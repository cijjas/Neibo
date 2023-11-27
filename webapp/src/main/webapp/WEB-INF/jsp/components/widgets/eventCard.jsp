<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="card-container">
    <div class="post-section">
        <div class="post-info">
            <div class="f-c-s-s g-05">
                <span class="font-size-24 mb-2 c-text"><c:out value="${event.name}"/></span>
                <p style="font-size: 12px; font-weight: normal"><c:out value="${event.description}"/></p>
                <div class="divider"></div>
                <div class="f-c-s-s g-05 ">
                    <span class=" font-size-16 font-weight-normal">
                        <spring:message code="date.format">
                            <spring:argument value="${selectedDay}"/>
                            <spring:argument value="${selectedMonth}"/>
                            <spring:argument value="${fn:replace(selectedYear, '.', '')}"/>
                        </spring:message>
                    </span>
                    <span class=" font-size-16 font-weight-bolder c-text">
                        <c:out value="${event.getStartTimeString()}"/> - <c:out value="${event.getEndTimeString()}"/>
                    </span>

                </div>
                <div class="divider"></div>
                <div class="postcard-description">

                    <spring:message code="Duration"/>: <c:out value="${event.getDuration()}"/> <spring:message
                        code="Minutes"/>
                </div>
            </div>

        </div>

        <%-- Button for attending--%>
        <c:choose>
            <c:when test="${willAttend == false}">
                <div class="d-flex flex-column justify-content-center align-items-end">
                    <form method="post" action="${pageContext.request.contextPath}/attend/${event.eventId}">
                        <div class="d-flex flex-row justify-content-end align-items-center">
                            <div class="d-flex flex-column justify-content-center align-items-end">
                                <button id="submitAttendance" type="submit" class="accept-button outlined"
                                        style="margin-top:5px; font-size: 12px; font-weight: bold;">
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
                                <button id="submitUnattendance" type="submit" class="ignore-button outlined"
                                        style="margin-top:5px; font-size: 12px;">
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
            <h3 class="c-primary" style="text-align: center;"><spring:message code="Attendees"/> (${attendees.size()})</h3>

            <div class="divider"></div>
            <c:choose>
                <c:when test="${empty attendees}">
                    <div class="postcard-description">
                        <spring:message code="NoAttendees"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="f-c-c-c w-100 g-05 p-3">
                        <c:forEach items="${attendees}" var="attendee" varStatus="loopStatus">
                            <div class="font-size-14 font-weight-normal c-text">
                                <c:out value="${attendee.name} "/><c:out value="${attendee.surname}"/>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>

</div>