package com.hotel.util;

import com.hotel.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static User getLoggedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("loggedUser");
    }

    public static boolean hasRole(HttpServletRequest req, String role) {
        User user = getLoggedUser(req);
        return user != null && role.equals(user.getRole());
    }
}