package com.hotel.servlet;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.UserRepository;
import com.hotel.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin-bookings")
public class AdminBookingServlet extends HttpServlet {

    private final BookingRepository bookingRepository = new BookingRepository();
    private final RoomRepository roomRepository = new RoomRepository();
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.hasRole(req, "ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("q") == null ? "" : req.getParameter("q").trim();
        String status = req.getParameter("status") == null ? "" : req.getParameter("status").trim().toUpperCase();
        String key = keyword.toLowerCase();

        // lookup maps so the page can show names instead of ids
        Map<String, User> users = new HashMap<>();
        for (User u : userRepository.search("")) {
            users.put(u.getId(), u);
        }
        Map<String, Room> rooms = new HashMap<>();
        for (Room r : roomRepository.findAll()) {
            rooms.put(r.getId(), r);
        }

        List<Booking> results = new ArrayList<>();
        for (Booking b : bookingRepository.findAll()) {
            if (!status.isEmpty() && !b.getStatus().equals(status)) {
                continue;
            }
            if (!key.isEmpty() && !matches(b, key, users.get(b.getUserId()), rooms.get(b.getRoomId()))) {
                continue;
            }
            results.add(b);
        }
        // newest stay first
        results.sort(Comparator.comparing(Booking::getCheckIn).reversed());

        req.setAttribute("bookings", results);
        req.setAttribute("users", users);
        req.setAttribute("rooms", rooms);
        req.setAttribute("keyword", keyword);
        req.setAttribute("status", status);
        req.getRequestDispatcher("/admin-bookings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!AuthUtil.hasRole(req, "ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String base = req.getContextPath() + "/admin-bookings";

        if ("cancel".equals(req.getParameter("action"))) {
            try {
                bookingRepository.cancel(req.getParameter("id"), null);   // null = admin, no owner check
                resp.sendRedirect(base + "?msg=cancelled");
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(base + "?err=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        resp.sendRedirect(base);
    }

    // Matches booking id, customer name or email, or room number
    private boolean matches(Booking b, String key, User user, Room room) {
        if (b.getId().toLowerCase().contains(key)) {
            return true;
        }
        if (user != null && (user.getFullName().toLowerCase().contains(key)
                || user.getEmail().toLowerCase().contains(key))) {
            return true;
        }
        return room != null && room.getRoomNumber().toLowerCase().contains(key);
    }
}
