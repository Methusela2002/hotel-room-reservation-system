package com.hotel.util;
import java.time.LocalDate;

public final class ValidationUtil {

    private ValidationUtil() {
    }

    // Returns an error message, or null when everything is valid
    public static String profileError(String fullName, String email, String phone) {
        if (isBlank(fullName) || isBlank(email) || isBlank(phone)) {
            return "Name, email and phone are required";
        }
        if (!phone.matches("^0\\d{9}$")) {
            return "Phone must be 10 digits and start with 0";
        }
        return null;
    }

    public static String passwordError(String password) {
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }
    // Returns an error message, or null when the stay is valid
    public static String stayError(LocalDate checkIn, LocalDate checkOut, int guests) {
        if (checkIn == null || checkOut == null) {
            return "Check-in and check-out dates are required";
        }
        if (checkIn.isBefore(LocalDate.now())) {
            return "Check-in date cannot be in the past";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "Check-out must be after check-in";
        }
        if (checkIn.plusDays(30).isBefore(checkOut)) {
            return "A stay cannot be longer than 30 nights";
        }
        if (guests < 1 || guests > 10) {
            return "Guests must be between 1 and 10";
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}