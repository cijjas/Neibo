<!-- Your existing HTML code here -->

<div class="upper-feed-buttons-box">
    <h2>
        current date
    </h2>
    <c:if test="${isAdmin}">
        <a href="${pageContext.request.contextPath}/admin/addEvent" class="cool-button">
            <spring:message code="CreateNewEvent.button" />
            <i class="fa-solid fa-plus"></i>
        </a>
    </c:if>
</div>

<%--<script>--%>
<%--    const cd = new Date(${timestamp});--%>
<%--    const day = cd.getDate();--%>
<%--    const month = cd.getMonth(); // Use the numeric month index--%>
<%--    const year = cd.getFullYear();--%>

<%--    // Define an array of localized month names on the server-side--%>
<%--    const localizedMonths = [--%>
<%--        "<spring:message code='month.january' />",--%>
<%--        "<spring:message code='month.february' />",--%>
<%--        "<spring:message code='month.march' />",--%>
<%--        "<spring:message code='month.april' />",--%>
<%--        "<spring:message code='month.may' />",--%>
<%--        "<spring:message code='month.june' />",--%>
<%--        "<spring:message code='month.july' />",--%>
<%--        "<spring:message code='month.august' />",--%>
<%--        "<spring:message code='month.september' />",--%>
<%--        "<spring:message code='month.october' />",--%>
<%--        "<spring:message code='month.november' />",--%>
<%--        "<spring:message code='month.december' />"--%>
<%--    ];--%>

<%--    // Create a h2 element with dynamic content--%>
<%--    const h2Element = document.querySelector("h2");--%>
<%--    h2Element.innerHTML = `<spring:message code="date.format" arguments="${day} ${month} ${year}"/>`;--%>

<%--</script>--%>
