<!DOCTYPE html>
<html>
<head>
    <title>Log In</title>
</head>
<body>
<h1>Logging innnnnnn</h1>
<form action="${pageContext.request.contextPath}/login" method="post">
    <label for="mail">Email:</label>
    <input type="email" id="mail" name="mail" required><br>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br>

    <label>Remember Me!</label>
    <label>
        <input name="rememberMe" type="checkbox">
    </label>

    <input type="submit" value="Log in!">
</form>
</body>
</html>
