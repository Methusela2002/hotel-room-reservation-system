<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${editUser eq null}">
    <c:redirect url="/admin-users"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit User - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark px-3">
    <span class="navbar-brand">Hotel Reservation - Admin</span>
    <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
</nav>

<div class="container py-4" style="max-width: 520px;">
    <h3 class="mb-3">Edit user <c:out value="${editUser.id}"/></h3>

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="admin-users" class="card card-body shadow-sm">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="id" value="<c:out value='${editUser.id}'/>">

        <div class="mb-3">
            <label class="form-label">Role</label>
            <input type="text" class="form-control" value="<c:out value='${editUser.role}'/>" disabled>
        </div>
        <div class="mb-3">
            <label class="form-label">Full name</label>
            <input type="text" name="fullName" class="form-control" value="<c:out value='${editUser.fullName}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" value="<c:out value='${editUser.email}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input type="text" name="phone" class="form-control" value="<c:out value='${editUser.phone}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">New password (leave blank to keep current)</label>
            <input type="password" name="newPassword" class="form-control" minlength="6">
        </div>
        <div>
            <button type="submit" class="btn btn-primary">Save changes</button>
            <a href="admin-users" class="btn btn-outline-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>