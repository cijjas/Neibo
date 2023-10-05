


    <script>
        function showPostsAfterDelay() {
            document.getElementById('placeholder-posts-container').style.display = 'none';
            document.getElementById('actual-posts-container').style.display = 'block';
        }

        setTimeout(showPostsAfterDelay, 1000);
    </script>

    <div id="placeholder-posts-container">
        <c:forEach begin="1" end="10" var="index">
            <%@ include file="/WEB-INF/jsp/components/widgets/placeholderBlogpost.jsp" %>
        </c:forEach>
    </div>


    <div id="actual-posts-container">
        <c:choose>
            <c:when test="${empty eventList}">
                <div class="no-posts-found">
                    <i class="circle-icon fa-solid fa-magnifying-glass"></i>
                    <spring:message code="Events.notFound"/>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="event" items="${eventList}" >
                        <jsp:include page="/WEB-INF/jsp/components/widgets/calendar/event.jsp" >
                            <jsp:param name="eventId" value="${event.eventId}" />
                            <jsp:param name="eventName" value="${event.name}"/>
                            <jsp:param name="eventDescription" value="${event.description}"/>
                            <jsp:param name="eventDate" value="${event.date}"/>
                            <jsp:param name="eventDuration" value="${event.duration}"/>
                        </jsp:include>

                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>


