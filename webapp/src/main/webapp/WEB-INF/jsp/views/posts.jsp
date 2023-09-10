<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>

<body>
<h1>List of Posts</h1>

    <table>

        <tbody>
        <c:forEach var="post" items="${postList}">
            <tr>
                <td>${post.date}</td>
                <td>${post.title}</td>
                <td>${post.description}</td>


                <!-- Add more table cells for other post attributes -->
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>
