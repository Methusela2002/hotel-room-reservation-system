package com.hotel.servlet;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.AuthUtil;
import com.hotel.util.PasswordUtil;
import com.hotel.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User logged = AuthUtil.getLoggedUser(req);
        if (logged == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // Always show the saved data from the file, not a stale session copy
        User fresh = userRepository.findById(logged.getId());
        if (fresh == null) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("formUser", fresh);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User logged = AuthUtil.getLoggedUser(req);
        if (logged == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = userRepository.findById(logged.getId());
        if (user == null) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        user.setFullName(valueOf(req.getParameter("fullName")));
        user.setEmail(valueOf(req.getParameter("email")));
        user.setPhone(valueOf(req.getParameter("phone")));

        String error = ValidationUtil.profileError(user.getFullName(), user.getEmail(), user.getPhone());

        // Changing the password requires the current password
        String newPassword = req.getParameter("newPassword");
        if (error == null && newPassword != null && !newPassword.isEmpty()) {
            String current = req.getParameter("currentPassword");
            if (current == null || !PasswordUtil.matches(current, logged.getPassword())) {
                error = "Current password is incorrect";
            } else {
                error = ValidationUtil.passwordError(newPassword);
                if (error == null) {
                    user.setPassword(PasswordUtil.hash(newPassword));
                }
            }
        }

        if (error == null) {
            try {
                userRepository.update(user);
                req.getSession().setAttribute("loggedUser", user);
                resp.sendRedirect(req.getContextPath() + "/profile?msg=updated");
                return;
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }

        req.setAttribute("error", error);
        req.setAttribute("formUser", user);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }

    private String valueOf(String value) {
        return value == null ? "" : value.trim();
    }
}