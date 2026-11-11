<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${empty sessionScope.loggedUser}">
    <c:redirect url="/login"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Find a Room - Hotel Reservation</title>
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

<div class="container py-4">
    <h3 class="mb-3">Find a room</h3>

    <c:if test="${not empty param.bookErr}">
        <div class="alert alert-danger"><c:out value="${param.bookErr}"/></div>
    </c:if>


    <form method="get" action="search-rooms" class="card card-body shadow-sm mb-4">
        <div class="row g-3 align-items-end">
            <div class="col-sm-6 col-md-3">
                <label class="form-label">Check-in</label>
                <input type="date" name="checkIn" class="form-control" value="<c:out value='${checkIn}'/>" required>
            </div>
            <div class="col-sm-6 col-md-3">
                <label class="form-label">Check-out</label>
                <input type="date" name="checkOut" class="form-control" value="<c:out value='${checkOut}'/>" required>
            </div>
            <div class="col-sm-6 col-md-2">
                <label class="form-label">Guests</label>
                <input type="number" name="guests" class="form-control" min="1" max="10"
                       value="<c:out value='${guests}'/>" required>
            </div>
            <div class="col-sm-6 col-md-2">
                <label class="form-label">Room type</label>
                <select name="type" class="form-select">
                    <option value="">Any</option>
                    <option value="STANDARD" ${type eq 'STANDARD' ? 'selected' : ''}>Standard</option>
                    <option value="DELUXE" ${type eq 'DELUXE' ? 'selected' : ''}>Deluxe</option>
                    <option value="SUITE" ${type eq 'SUITE' ? 'selected' : ''}>Suite</option>
                </select>
            </div>
            <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100">Search</button>
            </div>
        </div>
    </form>

    <c:if test="${searched}">
        <h5 class="mb-3">
            <c:out value="${rooms.size()}"/> room(s) available for
            <c:out value="${nights}"/> night(s)
        </h5>

        <c:if test="${empty rooms}">
            <div class="alert alert-info">No rooms match your search. Try other dates, fewer guests or any room type.</div>
        </c:if>

        <div class="row g-3">
            <c:forEach var="r" items="${rooms}">
                <div class="col-md-6 col-lg-4">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between">
                                <h5 class="card-title mb-1">Room <c:out value="${r.roomNumber}"/></h5>
                                <span class="badge bg-secondary align-self-start"><c:out value="${r.type}"/></span>
                            </div>
                            <p class="text-muted small mb-2"><c:out value="${r.description}"/></p>
                            <p class="small mb-2"><strong>Amenities:</strong> <c:out value="${r.amenities}"/></p>
                            <p class="small mb-2">Sleeps up to <c:out value="${r.capacity}"/> guests</p>
                            <p class="mb-0">
                                LKR <fmt:formatNumber value="${r.pricePerNight}" pattern="#,##0.00"/> / night
                            </p>
                            <c:if test="${r.serviceChargeRate > 0}">
                                <p class="small text-muted mb-0">
                                    + <fmt:formatNumber value="${r.serviceChargeRate * 100}" pattern="0"/>% service charge
                                </p>
                            </c:if>
                        </div>
                                                <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                            <strong>Total: LKR
                                <fmt:formatNumber value="${r.calculateTotalPrice(nights)}" pattern="#,##0.00"/>
                            </strong>
                            <c:if test="${sessionScope.loggedUser.role eq 'CUSTOMER'}">
                                <form method="post" action="book" class="m-0"
                                      onsubmit="return confirm('Book room ${r.roomNumber} for ${nights} night(s)?');">
                                    <input type="hidden" name="roomId" value="${r.id}">
                                    <input type="hidden" name="checkIn" value="<c:out value='${checkIn}'/>">
                                    <input type="hidden" name="checkOut" value="<c:out value='${checkOut}'/>">
                                    <input type="hidden" name="guests" value="<c:out value='${guests}'/>">
                                    <input type="hidden" name="type" value="<c:out value='${type}'/>">
                                    <button type="submit" class="btn btn-success btn-sm">Book</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>
