<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${users eq null}">
    <c:redirect url="/admin-users"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Users - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark px-3">
    <span class="navbar-brand">Hotel Reservation - Admin</span>
    <div>
        <a href="admin-dashboard.jsp" class="btn btn-outline-light btn-sm me-2">Dashboard</a>
        <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-3">Manage users</h3>

    <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">User updated.</div></c:if>
    <c:if test="${param.msg == 'deleted'}"><div class="alert alert-success">User deleted.</div></c:if>
    <c:if test="${param.err == 'self'}"><div class="alert alert-danger">You cannot delete your own account.</div></c:if>
    <c:if test="${param.err == 'notfound'}"><div class="alert alert-danger">User not found.</div></c:if>

    <form method="get" action="admin-users" class="row g-2 mb-3">
        <div class="col-sm-8 col-md-6">
            <input type="text" name="q" class="form-control" placeholder="Search by id, name, email or phone"
                   value="<c:out value='${keyword}'/>">
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="admin-users" class="btn btn-outline-secondary">Clear</a>
        </div>
    </form>

    <div class="table-responsive">
        <table class="table table-striped table-bordered bg-white align-middle">
            <thead class="table-dark">
            <tr>
                <th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th style="width: 170px;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${users}">
                <tr>
                    <td><c:out value="${u.id}"/></td>
                    <td><c:out value="${u.fullName}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td><c:out value="${u.phone}"/></td>
                    <td><span class="badge bg-secondary"><c:out value="${u.role}"/></span></td>
                    <td>
                        <a href="admin-users?action=edit&id=${u.id}" class="btn btn-sm btn-outline-primary">Edit</a>
                        <c:choose>
                            <c:when test="${u.id eq sessionScope.loggedUser.id}">
                                <span class="text-muted ms-1">You</span>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="admin-users" class="d-inline"
                                      onsubmit="return confirm('Delete this user?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty users}">
                <tr><td colspan="6" class="text-center text-muted">No users found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>