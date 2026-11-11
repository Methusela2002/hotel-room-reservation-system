package com.hotel.servlet;

import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.RoomRepository;
import com.hotel.util.AuthUtil;
import com.hotel.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.hotel.repository.BookingRepository;
import java.util.Set;


@WebServlet("/search-rooms")
public class RoomSearchServlet extends HttpServlet {

    private final RoomRepository roomRepository = new RoomRepository();
    private final BookingRepository bookingRepository = new BookingRepository();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = AuthUtil.getLoggedUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String checkInText = req.getParameter("checkIn");
        String checkOutText = req.getParameter("checkOut");

        // First visit: just show the empty search form
        if (checkInText == null && checkOutText == null) {
            req.setAttribute("guests", 1);
            req.getRequestDispatcher("/search-rooms.jsp").forward(req, resp);
            return;
        }

        String type = req.getParameter("type") == null ? "" : req.getParameter("type").trim();
        int guests = parseGuests(req.getParameter("guests"));

        req.setAttribute("checkIn", checkInText);
        req.setAttribute("checkOut", checkOutText);
        req.setAttribute("type", type);
        req.setAttribute("guests", guests);

        LocalDate checkIn = parseDate(checkInText);
        LocalDate checkOut = parseDate(checkOutText);

        String error = ValidationUtil.stayError(checkIn, checkOut, guests);
        if (error != null) {
            req.setAttribute("error", error);
            req.getRequestDispatcher("/search-rooms.jsp").forward(req, resp);
            return;
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        List<Room> rooms = roomRepository.findAvailable(type, guests);

        // Hide rooms that already have an active booking overlapping these dates
        Set<String> booked = bookingRepository.findBookedRoomIds(checkIn, checkOut);
        rooms.removeIf(room -> booked.contains(room.getId()));


        req.setAttribute("nights", nights);
        req.setAttribute("rooms", rooms);
        req.setAttribute("searched", true);
        req.getRequestDispatcher("/search-rooms.jsp").forward(req, resp);
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());   // format yyyy-MM-dd
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private int parseGuests(String text) {
        try {
            return Integer.parseInt(text == null ? "" : text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
