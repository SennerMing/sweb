<%--
  Created by IntelliJ IDEA.
  User: liuxiangren
  Date: 2021/6/2
  Time: 9:16 下午
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SSM</title>
</head>
<body>
<form method="post" action="${pageContext.request.contextPath}/reg.action">
    <label for="userName"/><input id="userName" type="text" name="user.name"/><br>
    <label for="password"/><input id="password" type="password" name="user.password"/><br>
    <input type="submit" value="注册">
</form>
</body>
</html>
