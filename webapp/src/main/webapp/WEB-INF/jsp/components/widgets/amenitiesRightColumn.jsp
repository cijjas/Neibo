<%@ include file="/WEB-INF/jsp/components/widgets/calendar/calendarWidget.jsp" %>
<div class="grey-static-container m-t-40">
    <div class="column d-flex justify-content-center align-items-center">
        <h3 class="m-b-20"><spring:message code="MyReservations"/></h3>
        <c:choose>
            <c:when test="${reservationsList.size() == 0}">
                <div class="f-c-c-c align-items-center" style="text-align: center">
                    <span class="w-100 mb-2">
                        <spring:message code="No.reservations"/>
                    </span>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="reservation" items="${reservationsList}">
                    <div class="cool-static-container m-b-20" style="word-wrap: break-word;" aria-hidden="true">
                        <div class="f-c-c-c">
                            <div class="f-r-sb-c w-100">
                                <h5><c:out value="${reservation.amenityName}"/> </h5>

                                <a href="${pageContext.request.contextPath}/delete-reservation/${reservation.bookingId}" class="f-c-c-c">
                                    <i class="fas fa-trash" style="color: var(--error);"></i>
                                </a>
                            </div>
                            <div>
                                <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="Date"/> <c:out value="${reservation.date}" /></h6>
                                <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="StartTime"/> <c:out value="${reservation.startTime}" /></h6>
                                <h6 class="mb-3" style="color:var(--lighttext);"><spring:message code="EndTime"/> <c:out value="${reservation.endTime}" /></h6>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

    </div>
</div>