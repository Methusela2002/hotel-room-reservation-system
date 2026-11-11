<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${formRoom eq null}">
    <c:redirect url="/admin-rooms"/>
</c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${isNew ? 'Add' : 'Edit'} Room - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark px-3">
    <span class="navbar-brand">Hotel Reservation - Admin</span>
    <a href="logout" class="btn btn-outline-light btn-sm">Logout</a>
</nav>

<div class="container py-4" style="max-width: 560px;">
    <h3 class="mb-3">${isNew ? 'Add room' : 'Edit room'}</h3>

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="admin-rooms" class="card card-body shadow-sm">
        <input type="hidden" name="action" value="${isNew ? 'create' : 'update'}">
        <c:if test="${not isNew}">
            <input type="hidden" name="id" value="<c:out value='${formRoom.id}'/>">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Type</label>
            <c:choose>
                <c:when test="${isNew}">
                    <select name="type" class="form-select">
                        <option value="STANDARD" ${formRoom.type eq 'STANDARD' ? 'selected' : ''}>Standard</option>
                        <option value="DELUXE" ${formRoom.type eq 'DELUXE' ? 'selected' : ''}>Deluxe</option>
                        <option value="SUITE" ${formRoom.type eq 'SUITE' ? 'selected' : ''}>Suite</option>
                    </select>
                </c:when>
                <c:otherwise>
                    <input type="text" class="form-control" value="<c:out value='${formRoom.type}'/>" disabled>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="mb-3">
            <label class="form-label">Room number</label>
            <input type="text" name="roomNumber" class="form-control"
                   value="<c:out value='${formRoom.roomNumber}'/>" required>
        </div>
        <div class="row">
            <div class="col-sm-6 mb-3">
                <label class="form-label">Price per night (LKR)</label>
                <input type="number" name="pricePerNight" class="form-control" min="1" step="0.01"
                       value="${formRoom.pricePerNight > 0 ? formRoom.pricePerNight : ''}" required>
            </div>
            <div class="col-sm-6 mb-3">
                <label class="form-label">Capacity (guests)</label>
                <input type="number" name="capacity" class="form-control" min="1" max="10"
                       value="${formRoom.capacity}" required>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea name="description" class="form-control" rows="2"><c:out value="${formRoom.description}"/></textarea>
        </div>
        <div class="form-check mb-3">
            <input type="checkbox" name="inService" id="inService" class="form-check-input"
                   ${formRoom.inService ? 'checked' : ''}>
            <label for="inService" class="form-check-label">In service (uncheck for maintenance)</label>
        </div>
        <div>
            <button type="submit" class="btn btn-primary">${isNew ? 'Add room' : 'Save changes'}</button>
            <a href="admin-rooms" class="btn btn-outline-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
