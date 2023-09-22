<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>User Registration</h1>
<form action="${pageContext.request.contextPath}/signup" method="post">
    <label for="name">First Name:</label>
    <input type="text" id="name" name="name" required><br>

    <label for="surname">Last Name:</label>
    <input type="text" id="surname" name="surname" required><br>

    <label for="mail">Email:</label>
    <input type="email" id="mail" name="mail" required><br>

    <div class="form-input">
        <label>Neighborhoods</label>
        <select name="neighborhoodId" class="form-control">
            <c:forEach var="entry" items="${neighborhoodsList}">
                <option value="${entry.neighborhoodId}">${entry.getName()}</option>
            </c:forEach>
        </select>
    </div>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br>

    <input type="submit" value="Register">
</form>
</body>
</html>
