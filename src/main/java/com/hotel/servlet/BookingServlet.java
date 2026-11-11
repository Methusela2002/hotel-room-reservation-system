package com.hotel.servlet;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.util.AuthUtil;
import com.hotel.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@WebServlet("/book")
public class BookingServlet extends HttpServlet {

    private final BookingRepository bookingRepository = new BookingRepository();
    private final RoomRepository roomRepository = new RoomRepository();

    // GET /book?id=B001 shows the confirmation page of one of the customer's own bookings
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.hasRole(req, "CUSTOMER")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User user = AuthUtil.getLoggedUser(req);

        Booking booking = bookingRepository.findById(req.getParameter("id"));
        if (booking == null || !booking.getUserId().equals(user.getId())) {
            resp.sendRedirect(req.getContextPath() + "/customer-home.jsp");
            return;
        }
        req.setAttribute("booking", booking);
        req.setAttribute("room", roomRepository.findById(booking.getRoomId()));
        req.getRequestDispatcher("/booking-confirmation.jsp").forward(req, resp);
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

        String roomId = req.getParameter("roomId");
        String checkInText = req.getParameter("checkIn");
        String checkOutText = req.getParameter("checkOut");
        String type = req.getParameter("type");
        int guests = parseGuests(req.getParameter("guests"));

        LocalDate checkIn = parseDate(checkInText);
        LocalDate checkOut = parseDate(checkOutText);

        String error = ValidationUtil.stayError(checkIn, checkOut, guests);
        Room room = null;
        if (error == null) {
            room = roomRepository.findById(roomId);
            if (room == null || !room.isInService()) {
                error = "This room is not available";
            } else if (room.getCapacity() < guests) {
                error = "This room is too small for " + guests + " guests";
            }
        }

        if (error == null) {
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            // The price is always calculated on the server, never taken from the form
            double total = room.calculateTotalPrice((int) nights);
            Booking booking = new Booking(null, user.getId(), room.getId(),
                    checkIn, checkOut, guests, total, Booking.CONFIRMED);
            try {
                bookingRepository.save(booking);
                resp.sendRedirect(req.getContextPath() + "/book?id=" + booking.getId());
                return;
            } catch (IllegalArgumentException e) {
                error = e.getMessage();
            }
        }

        // Back to the search results with the error message
        resp.sendRedirect(req.getContextPath() + "/search-rooms"
                + "?checkIn=" + encode(checkInText)
                + "&checkOut=" + encode(checkOutText)
                + "&guests=" + guests
                + "&type=" + encode(type)
                + "&bookErr=" + encode(error));
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim());
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

    private String encode(String value) throws IOException {
        return URLEncoder.encode(value == null ? "" : value, "UTF-8");
    }
}
