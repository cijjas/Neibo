<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <!-- Your HTML head content here -->
</head>
<body>
<h1>Publish!!!</h1>

<form:form method="post" action="/publish" modelAttribute="neighborPostWrapper">
    <label for="mail">Email:</label>
    <form:input type="text" path="neighbor.mail" id="mail" /><br/>

    <label for="name">First Name:</label>
    <form:input type="text" path="neighbor.name" id="name" /><br/>

    <label for="surname">Last Name:</label>
    <form:input type="text" path="neighbor.surname" id="surname" /><br/>

    <label for="neighborhood">Neighborhood:</label>
    <form:input type="text" path="neighborhood.name" id="neighborhood" /><br/>

    <label for="mail">Title:</label>
    <form:input type="text" path="post.title" id="title" /><br/>

    <label for="name">Description:</label>
    <form:input type="text" path="post.description" id="description" /><br/>

    <button type="submit">Publish</button>

</form:form>

</body>
</html>
