package com.hotel.util;

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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}