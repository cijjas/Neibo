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
<form name="shiftForm" action="${pageContext.request.contextPath}/testAmenityCreation" method="post">
    <!-- Your other form elements here -->

    <h2>Available Shifts</h2>
    <table>
        <tr>
            <th>Day</th>
            <th>Time</th>
            <th>Book?</th>
        </tr>
        <c:forEach items="${daysPairs}" var="day">
            <c:forEach items="${timesPairs}" var="time">
                <tr>
                    <td>${day.value}</td>
                    <td>${time.value}</td>
                    <td>
                        <label>
                            <input type="checkbox" name="selectedShifts" value="${day.key},${time.key}" />
                            Book?
                        </label>
                    </td>
                </tr>
            </c:forEach>
        </c:forEach>
    </table>
    <br />
    <input type="submit" value="Book Selected Shifts" />
</form>
</body>
</html>
