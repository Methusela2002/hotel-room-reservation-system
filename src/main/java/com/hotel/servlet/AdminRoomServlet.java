package com.hotel.servlet;

import com.hotel.model.DeluxeRoom;
import com.hotel.model.Room;
import com.hotel.model.StandardRoom;
import com.hotel.model.SuiteRoom;
import com.hotel.repository.RoomRepository;
import com.hotel.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.hotel.repository.BookingRepository;


@WebServlet("/admin-rooms")
public class AdminRoomServlet extends HttpServlet {

    private final RoomRepository roomRepository = new RoomRepository();
    private final BookingRepository bookingRepository = new BookingRepository();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!AuthUtil.hasRole(req, "ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");

        if ("add".equals(action)) {
            req.setAttribute("formRoom", new StandardRoom(null, "", 0, 2, true, ""));
            req.setAttribute("isNew", true);
            req.getRequestDispatcher("/admin-room-form.jsp").forward(req, resp);
            return;
        }

        if ("edit".equals(action)) {
            Room room = roomRepository.findById(req.getParameter("id"));
            if (room == null) {
                resp.sendRedirect(req.getContextPath() + "/admin-rooms?err=notfound");
                return;
            }
            req.setAttribute("formRoom", room);
            req.setAttribute("isNew", false);
            req.getRequestDispatcher("/admin-room-form.jsp").forward(req, resp);
            return;
        }

        String keyword = req.getParameter("q") == null ? "" : req.getParameter("q").trim();
        String type = req.getParameter("type") == null ? "" : req.getParameter("type").trim();
        req.setAttribute("rooms", roomRepository.search(keyword, type));
        req.setAttribute("keyword", keyword);
        req.setAttribute("type", type);
        req.getRequestDispatcher("/admin-rooms.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (!AuthUtil.hasRole(req, "ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String base = req.getContextPath() + "/admin-rooms";
        String action = req.getParameter("action");
        String id = req.getParameter("id");

        // ---- DELETE ----
        if ("delete".equals(action)) {
            boolean removed = roomRepository.delete(id);
            resp.sendRedirect(base + (removed ? "?msg=deleted" : "?err=notfound"));
            return;
        }

        // ---- TOGGLE MAINTENANCE ----
        if ("toggle".equals(action)) {
            Room room = roomRepository.findById(id);
            if (room == null) {
                resp.sendRedirect(base + "?err=notfound");
                return;
            }
            room.setInService(!room.isInService());
            roomRepository.update(room);
            resp.sendRedirect(base + "?msg=" + (room.isInService() ? "inservice" : "maintenance"));
            return;
        }

        // ---- CREATE / UPDATE ----
        if ("create".equals(action) || "update".equals(action)) {
            boolean isNew = "create".equals(action);
            Room room;
            String error = null;

            if (isNew) {
                room = newRoomOfType(req.getParameter("type"));
            } else {
                room = roomRepository.findById(id);
                if (room == null) {
                    resp.sendRedirect(base + "?err=notfound");
                    return;
                }
            }

            room.setRoomNumber(valueOf(req.getParameter("roomNumber")));
            room.setDescription(valueOf(req.getParameter("description")));
            room.setInService(req.getParameter("inService") != null);
            try {
                room.setPricePerNight(Double.parseDouble(valueOf(req.getParameter("pricePerNight"))));
                room.setCapacity(Integer.parseInt(valueOf(req.getParameter("capacity"))));
            } catch (NumberFormatException e) {
                error = "Price and capacity must be valid numbers";
            }

            if (error == null) {
                try {
                    if (isNew) {
                        roomRepository.save(room);
                    } else if (!roomRepository.update(room)) {
                        resp.sendRedirect(base + "?err=notfound");
                        return;
                    }
                    resp.sendRedirect(base + "?msg=" + (isNew ? "created" : "updated"));
                    return;
                } catch (IllegalArgumentException e) {
                    error = e.getMessage();
                }
            }

            req.setAttribute("error", error);
            req.setAttribute("formRoom", room);
            req.setAttribute("isNew", isNew);
            req.getRequestDispatcher("/admin-room-form.jsp").forward(req, resp);
            return;
        }

        resp.sendRedirect(base);
    }

    // Polymorphism in use: the chosen type decides which subclass is created
    private Room newRoomOfType(String type) {
        if ("DELUXE".equals(type)) {
            return new DeluxeRoom();
        } else if ("SUITE".equals(type)) {
            return new SuiteRoom();
        }
        return new StandardRoom();
    }

    private String valueOf(String value) {
        return value == null ? "" : value.trim();
    }
}
