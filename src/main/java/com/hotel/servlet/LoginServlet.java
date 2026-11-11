package com.hotel.servlet;

import com.hotel.model.User;
import com.hotel.repository.UserRepository;
import com.hotel.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            User user = (User) session.getAttribute("loggedUser");
            resp.sendRedirect(req.getContextPath() + "/" + user.getHomePage());
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email") == null ? "" : req.getParameter("email").trim();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        User user = userRepository.findByEmail(email);

        if (user == null || !PasswordUtil.matches(password, user.getPassword())) {
            req.setAttribute("error", "Invalid email or password");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        // Start a fresh session on login
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("loggedUser", user);

        // Polymorphism: Admin and Customer each return their own home page
        resp.sendRedirect(req.getContextPath() + "/" + user.getHomePage());
    }
}