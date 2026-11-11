<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty sessionScope.loggedUser or sessionScope.loggedUser.role ne 'ADMIN'}">
    <c:redirect url="/login"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark px-3">
    <span class="navbar-brand">Hotel Reservation - Admin</span>
    <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
</nav>
<div class="container py-4">
    <h3>Admin dashboard</h3>
    <p class="text-muted">Signed in as <c:out value="${sessionScope.loggedUser.fullName}"/>. User and room management will appear here soon.</p>
</div>
</body>
</html>