<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${rooms eq null}">
    <c:redirect url="/admin-rooms"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Rooms - Hotel Reservation</title>
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
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">Manage rooms</h3>
        <a href="admin-rooms?action=add" class="btn btn-success">+ Add room</a>
    </div>

    <c:if test="${param.msg == 'created'}"><div class="alert alert-success">Room added.</div></c:if>
    <c:if test="${param.msg == 'updated'}"><div class="alert alert-success">Room updated.</div></c:if>
    <c:if test="${param.msg == 'deleted'}"><div class="alert alert-success">Room deleted.</div></c:if>
    <c:if test="${param.msg == 'maintenance'}"><div class="alert alert-warning">Room moved to maintenance.</div></c:if>
    <c:if test="${param.msg == 'inservice'}"><div class="alert alert-success">Room is back in service.</div></c:if>
    <c:if test="${param.err == 'hasbookings'}"><div class="alert alert-danger">This room has active upcoming bookings and cannot be deleted. Cancel those bookings first, or put the room in maintenance.</div></c:if>


    <form method="get" action="admin-rooms" class="row g-2 mb-3">
        <div class="col-sm-6 col-md-5">
            <input type="text" name="q" class="form-control" placeholder="Search by room number, type or description"
                   value="<c:out value='${keyword}'/>">
        </div>
        <div class="col-sm-4 col-md-3">
            <select name="type" class="form-select">
                <option value="">All types</option>
                <option value="STANDARD" ${type eq 'STANDARD' ? 'selected' : ''}>Standard</option>
                <option value="DELUXE" ${type eq 'DELUXE' ? 'selected' : ''}>Deluxe</option>
                <option value="SUITE" ${type eq 'SUITE' ? 'selected' : ''}>Suite</option>
            </select>
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="admin-rooms" class="btn btn-outline-secondary">Clear</a>
        </div>
    </form>

    <div class="table-responsive">
        <table class="table table-striped table-bordered bg-white align-middle">
            <thead class="table-dark">
            <tr>
                <th>ID</th><th>Room</th><th>Type</th><th>Price / night</th><th>Guests</th>
                <th>Status</th><th>Description</th><th style="width: 260px;">Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="r" items="${rooms}">
                <tr>
                    <td><c:out value="${r.id}"/></td>
                    <td><c:out value="${r.roomNumber}"/></td>
                    <td><span class="badge bg-secondary"><c:out value="${r.type}"/></span></td>
                    <td>LKR <c:out value="${r.pricePerNight}"/></td>
                    <td><c:out value="${r.capacity}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${r.inService}"><span class="badge bg-success">In service</span></c:when>
                            <c:otherwise><span class="badge bg-warning text-dark">Maintenance</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${r.description}"/></td>
                    <td>
                        <a href="admin-rooms?action=edit&id=${r.id}" class="btn btn-sm btn-outline-primary">Edit</a>
                        <form method="post" action="admin-rooms" class="d-inline">
                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="id" value="${r.id}">
                            <button type="submit" class="btn btn-sm btn-outline-warning">
                                ${r.inService ? 'Maintenance' : 'Activate'}
                            </button>
                        </form>
                        <form method="post" action="admin-rooms" class="d-inline"
                              onsubmit="return confirm('Delete this room?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${r.id}">
                            <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty rooms}">
                <tr><td colspan="8" class="text-center text-muted">No rooms found.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
