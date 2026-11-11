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

@WebServlet("/admin-users")
public class AdminUserServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.hasRole(req, "ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("edit".equals(req.getParameter("action"))) {
            User user = userRepository.findById(req.getParameter("id"));
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/admin-users?err=notfound");
                return;
            }
            req.setAttribute("editUser", user);
            req.getRequestDispatcher("/admin-user-edit.jsp").forward(req, resp);
            return;
        }

        String keyword = req.getParameter("q") == null ? "" : req.getParameter("q").trim();
        req.setAttribute("users", userRepository.search(keyword));
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/admin-users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        User admin = AuthUtil.getLoggedUser(req);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String base = req.getContextPath() + "/admin-users";
        String action = req.getParameter("action");
        String id = req.getParameter("id");

        // ---- DELETE ----
        if ("delete".equals(action)) {
            if (admin.getId().equals(id)) {
                resp.sendRedirect(base + "?err=self");
                return;
            }
            boolean removed = userRepository.delete(id);
            resp.sendRedirect(base + (removed ? "?msg=deleted" : "?err=notfound"));
            return;
        }

        // ---- UPDATE ----
        if ("update".equals(action)) {
            User user = userRepository.findById(id);
            if (user == null) {
                resp.sendRedirect(base + "?err=notfound");
                return;
            }

            user.setFullName(valueOf(req.getParameter("fullName")));
            user.setEmail(valueOf(req.getParameter("email")));
            user.setPhone(valueOf(req.getParameter("phone")));

            String error = ValidationUtil.profileError(user.getFullName(), user.getEmail(), user.getPhone());

            // A blank password box means "keep the current password"
            String newPassword = req.getParameter("newPassword");
            if (error == null && newPassword != null && !newPassword.isEmpty()) {
                error = ValidationUtil.passwordError(newPassword);
                if (error == null) {
                    user.setPassword(PasswordUtil.hash(newPassword));
                }
            }

            if (error == null) {
                try {
                    userRepository.update(user);
                    if (user.getId().equals(admin.getId())) {
                        req.getSession().setAttribute("loggedUser", user);
                    }
                    resp.sendRedirect(base + "?msg=updated");
                    return;
                } catch (IllegalArgumentException e) {
                    error = e.getMessage();
                }
            }

            req.setAttribute("error", error);
            req.setAttribute("editUser", user);
            req.getRequestDispatcher("/admin-user-edit.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(base);
    }

    private String valueOf(String value) {
        return value == null ? "" : value.trim();
    }
}