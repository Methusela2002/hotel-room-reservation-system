<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${formUser eq null}">
    <c:redirect url="/profile"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Profile - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-primary px-3">
    <span class="navbar-brand">Hotel Reservation</span>
    <div>
        <a href="${sessionScope.loggedUser.homePage}" class="btn btn-outline-light btn-sm me-2">Home</a>
        <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container py-4" style="max-width: 520px;">
    <h3 class="mb-3">My profile</h3>

    <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">Profile updated.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>

    <form method="post" action="profile" class="card card-body shadow-sm">
        <div class="mb-3">
            <label class="form-label">Full name</label>
            <input type="text" name="fullName" class="form-control" value="<c:out value='${formUser.fullName}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" value="<c:out value='${formUser.email}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input type="text" name="phone" class="form-control" value="<c:out value='${formUser.phone}'/>" required>
        </div>
        <hr>
        <p class="text-muted small mb-2">To change your password, fill in both boxes. Otherwise leave them blank.</p>
        <div class="mb-3">
            <label class="form-label">Current password</label>
            <input type="password" name="currentPassword" class="form-control">
        </div>
        <div class="mb-3">
            <label class="form-label">New password</label>
            <input type="password" name="newPassword" class="form-control" minlength="6">
        </div>
        <button type="submit" class="btn btn-primary">Save changes</button>
    </form>
</div>
</body>
</html>