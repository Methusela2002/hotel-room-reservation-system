<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${booking eq null}">
    <c:redirect url="/customer-home.jsp"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Booking Confirmed - Hotel Reservation</title>
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

<div class="container py-4" style="max-width: 560px;">
    <div class="alert alert-success">Your booking is confirmed.</div>

    <div class="card shadow-sm">
        <div class="card-body">
            <h5 class="card-title">Booking <c:out value="${booking.id}"/></h5>
            <table class="table table-sm mb-0">
                <tr><th>Room</th>
                    <td><c:out value="${room.roomNumber}"/> (<c:out value="${room.type}"/>)</td></tr>
                <tr><th>Check-in</th><td><c:out value="${booking.checkIn}"/></td></tr>
                <tr><th>Check-out</th><td><c:out value="${booking.checkOut}"/></td></tr>
                <tr><th>Nights</th><td><c:out value="${booking.nights}"/></td></tr>
                <tr><th>Guests</th><td><c:out value="${booking.guests}"/></td></tr>
                <tr><th>Total</th>
                    <td><strong>LKR <fmt:formatNumber value="${booking.totalPrice}" pattern="#,##0.00"/></strong></td></tr>
                <tr><th>Status</th><td><span class="badge bg-success"><c:out value="${booking.status}"/></span></td></tr>
            </table>
        </div>
    </div>
    <a href="search-rooms" class="btn btn-primary mt-3">Find another room</a>
</div>
</body>
</html>
