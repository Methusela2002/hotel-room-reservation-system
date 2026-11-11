package com.hotel.servlet;

import com.hotel.model.Customer;
import com.hotel.repository.UserRepository;
import com.hotel.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String fullName = trim(req.getParameter("fullName"));
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        String error = null;
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password == null || password.isEmpty()) {
            error = "All fields are required";
        } else if (!phone.matches("^0\\d{9}$")) {
            error = "Phone must be 10 digits and start with 0";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match";
        }

        if (error == null) {
            try {
                Customer customer = new Customer(null, fullName, email,
                        PasswordUtil.hash(password), phone);
                userRepository.save(customer);
                resp.sendRedirect(req.getContextPath() + "/login?registered=1");
                return;
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }

        req.setAttribute("error", error);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}