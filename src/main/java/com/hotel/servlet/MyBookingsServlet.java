package com.hotel.servlet;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/my-bookings")
public class MyBookingsServlet extends HttpServlet {

    private final BookingRepository bookingRepository = new BookingRepository();
    private final RoomRepository roomRepository = new RoomRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.hasRole(req, "CUSTOMER")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = AuthUtil.getLoggedUser(req);

        List<Booking> bookings = bookingRepository.findByUser(user.getId());
        // newest stay first
        bookings.sort(Comparator.comparing(Booking::getCheckIn).reversed());

        // roomId -> Room, so the page can show room number and type
        Map<String, Room> rooms = new HashMap<>();
        for (Room room : roomRepository.findAll()) {
            rooms.put(room.getId(), room);
        }

        req.setAttribute("bookings", bookings);
        req.setAttribute("rooms", rooms);
        req.getRequestDispatcher("/my-bookings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!AuthUtil.hasRole(req, "CUSTOMER")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = AuthUtil.getLoggedUser(req);
        String base = req.getContextPath() + "/my-bookings";

        if ("cancel".equals(req.getParameter("action"))) {
            try {
                bookingRepository.cancel(req.getParameter("id"), user.getId());
                resp.sendRedirect(base + "?msg=cancelled");
            } catch (IllegalArgumentException e) {
                resp.sendRedirect(base + "?err=" + URLEncoder.encode(e.getMessage(), "UTF-8"));
            }
            return;
        }

        resp.sendRedirect(base);
    }
}
