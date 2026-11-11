package com.hotel.repository;

import com.hotel.model.Booking;
import com.hotel.util.FileHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookingRepository {

    private static final String FILE = "bookings.txt";

    private static final Object WRITE_LOCK = new Object();

    // ---------- READ ----------

    public List<Booking> findAll() throws IOException {
        List<Booking> bookings = new ArrayList<>();
        for (String line : FileHandler.readAllLines(FILE)) {
            bookings.add(Booking.fromFileString(line));
        }
        return bookings;
    }

    public Booking findById(String id) throws IOException {
        for (Booking b : findAll()) {
            if (b.getId().equals(id)) {
                return b;
            }
        }
        return null;
    }

    public List<Booking> findByUser(String userId) throws IOException {
        List<Booking> results = new ArrayList<>();
        for (Booking b : findAll()) {
            if (b.getUserId().equals(userId)) {
                results.add(b);
            }
        }
        return results;
    }

    // Ids of rooms that have an active booking overlapping [from, to)
    public Set<String> findBookedRoomIds(LocalDate from, LocalDate to) throws IOException {
        Set<String> ids = new HashSet<>();
        for (Booking b : findAll()) {
            if (b.overlaps(from, to)) {
                ids.add(b.getRoomId());
            }
        }
        return ids;
    }

    public boolean isRoomFree(String roomId, LocalDate from, LocalDate to) throws IOException {
        for (Booking b : findAll()) {
            if (b.getRoomId().equals(roomId) && b.overlaps(from, to)) {
                return false;
            }
        }
        return true;
    }

    // True if the room has a confirmed booking that has not finished yet.
    // Used to block deleting a room that guests still expect to use.
    public boolean hasActiveFutureBookings(String roomId) throws IOException {
        LocalDate today = LocalDate.now();
        for (Booking b : findAll()) {
            if (b.getRoomId().equals(roomId) && b.isActive() && b.getCheckOut().isAfter(today)) {
                return true;
            }
        }
        return false;
    }

    // ---------- CREATE ----------

    // The overlap check is repeated inside the lock, so two customers
    // booking the same room at the same moment cannot both succeed.
    public Booking save(Booking booking) throws IOException {
        synchronized (WRITE_LOCK) {
            if (booking.getCheckIn() == null || booking.getCheckOut() == null
                    || !booking.getCheckOut().isAfter(booking.getCheckIn())) {
                throw new IllegalArgumentException("Check-out must be after check-in");
            }
            if (!isRoomFree(booking.getRoomId(), booking.getCheckIn(), booking.getCheckOut())) {
                throw new IllegalArgumentException("This room is already booked for the selected dates");
            }
            booking.setStatus(Booking.CONFIRMED);
            booking.setId(nextId());
            FileHandler.appendLine(FILE, booking.toFileString());
            return booking;
        }
    }

        // ---------- UPDATE (cancel) ----------

    // userId = the customer cancelling their own booking; null = admin (no owner check)
    public Booking cancel(String bookingId, String userId) throws IOException {
        synchronized (WRITE_LOCK) {
            List<Booking> bookings = findAll();

            Booking target = null;
            for (Booking b : bookings) {
                if (b.getId().equals(bookingId)) {
                    target = b;
                    break;
                }
            }
            if (target == null || (userId != null && !target.getUserId().equals(userId))) {
                throw new IllegalArgumentException("Booking not found");
            }
            if (!target.isActive()) {
                throw new IllegalArgumentException("This booking is already cancelled");
            }
            if (!target.isCancellable()) {
                throw new IllegalArgumentException("Bookings that have already started cannot be cancelled");
            }

            target.setStatus(Booking.CANCELLED);
            writeBookings(bookings);
            return target;
        }
    }

    // ---------- helpers ----------
    private void writeBookings(List<Booking> bookings) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) {
            lines.add(b.toFileString());
        }
        FileHandler.writeAllLines(FILE, lines);
    }

    // B001, B002, ... highest existing number + 1
    private String nextId() throws IOException {
        int max = 0;
        for (Booking b : findAll()) {
            try {
                int number = Integer.parseInt(b.getId().substring(1));
                if (number > max) {
                    max = number;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // ignore ids that do not follow the Bnnn pattern
            }
        }
        return String.format("B%03d", max + 1);
    }
}
