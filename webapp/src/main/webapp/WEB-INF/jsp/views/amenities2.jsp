<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Select Shifts</title>
</head>
<body>
<h1>Select Shifts</h1>
<form name="shiftForm" action="${pageContext.request.contextPath}/testAmenityBooking" method="post">
    <input type="hidden" name="amenityId" value="${amenityId}" />
    <input type="hidden" name="date" value="${bookingDate}" />

    <h5>${bookings} </h5>

    <h2>Available Shifts for ${bookingDate}</h2>
    <table>
        <tr>
            <th>Shift</th>
            <th>Time</th>
            <th>Status</th>
        </tr>
        <c:forEach items="${bookings}" var="shift">
            <tr>
                <td>${shift.shiftId}</td>
                <td>${shift.startTime.timeInterval}</td>
                <td>
                    <c:choose>
                        <c:when test="${shift.taken}">
                            Unavailable
                        </c:when>
                        <c:otherwise>
                            <label>
                                <input type="checkbox" name="selectedShifts" value="${shift.shiftId}" /> Book?
                            </label>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
    <br />
    <input type="submit" value="Book Selected Shifts" />
</form>
</body>
</html>
