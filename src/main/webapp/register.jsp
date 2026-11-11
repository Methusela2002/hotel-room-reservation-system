<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register - Hotel Reservation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container py-5" style="max-width: 460px;">
    <h2 class="mb-4 text-center">Create an account</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="register" class="card card-body shadow-sm">
        <div class="mb-3">
            <label class="form-label">Full name</label>
            <input type="text" name="fullName" class="form-control" value="<c:out value='${param.fullName}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control" value="<c:out value='${param.email}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input type="text" name="phone" class="form-control" placeholder="0771234567" value="<c:out value='${param.phone}'/>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" minlength="6" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Confirm password</label>
            <input type="password" name="confirmPassword" class="form-control" minlength="6" required>
        </div>
        <button type="submit" class="btn btn-primary w-100">Register</button>
    </form>
    <p class="text-center mt-3">Already registered? <a href="login">Login</a></p>
</div>
</body>
</html>