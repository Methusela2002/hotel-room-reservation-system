<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${bookings eq null}">
    <c:redirect url="/my-bookings"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Bookings - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-primary px-3">
    <span class="navbar-brand">Hotel Reservation</span>
    <div>
        <a href="customer-home.jsp" class="btn btn-outline-light btn-sm me-2">Home</a>
        <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
    </div>
</nav>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3 class="mb-0">My bookings</h3>
        <a href="search-rooms" class="btn btn-primary">Find a room</a>
    </div>

    <c:if test="${param.msg == 'cancelled'}"><div class="alert alert-success">Booking cancelled.</div></c:if>
    <c:if test="${not empty param.err}"><div class="alert alert-danger"><c:out value="${param.err}"/></div></c:if>

    <div class="table-responsive">
        <table class="table table-striped table-bordered bg-white align-middle">
            <thead class="table-dark">
            <tr>
                <th>Booking</th><th>Room</th><th>Check-in</th><th>Check-out</th>
                <th>Nights</th><th>Guests</th><th>Total</th><th>Status</th><th style="width: 110px;">Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="b" items="${bookings}">
                <tr>
                    <td><c:out value="${b.id}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${empty rooms[b.roomId]}"><span class="text-muted">(room removed)</span></c:when>
                            <c:otherwise>
                                <c:out value="${rooms[b.roomId].roomNumber}"/>
                                <span class="badge bg-secondary"><c:out value="${rooms[b.roomId].type}"/></span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${b.checkIn}"/></td>
                    <td><c:out value="${b.checkOut}"/></td>
                    <td><c:out value="${b.nights}"/></td>
                    <td><c:out value="${b.guests}"/></td>
                    <td>LKR <fmt:formatNumber value="${b.totalPrice}" pattern="#,##0.00"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${b.active}"><span class="badge bg-success">CONFIRMED</span></c:when>
                            <c:otherwise><span class="badge bg-secondary">CANCELLED</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:if test="${b.cancellable}">
                            <form method="post" action="my-bookings" class="m-0"
                                  onsubmit="return confirm('Cancel booking ${b.id}?');">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="id" value="${b.id}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Cancel</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty bookings}">
                <tr><td colspan="9" class="text-center text-muted">You have no bookings yet.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
