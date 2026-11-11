package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Booking {

    public static final String CONFIRMED = "CONFIRMED";
    public static final String CANCELLED = "CANCELLED";

    private String id;
    private String userId;
    private String roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guests;
    private double totalPrice;
    private String status;

    public Booking() {
    }

    public Booking(String id, String userId, String roomId, LocalDate checkIn,
                   LocalDate checkOut, int guests, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public boolean isActive() {
        return CONFIRMED.equals(status);
    }

    // A booking can be cancelled while it is confirmed and has not started yet
    public boolean isCancellable() {
        return isActive() && checkIn.isAfter(LocalDate.now());
    }

    // True when this booking blocks the room for any night of [from, to)
    public boolean overlaps(LocalDate from, LocalDate to) {
        return isActive() && checkIn.isBefore(to) && from.isBefore(checkOut);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Format: id|userId|roomId|checkIn|checkOut|guests|totalPrice|status
    public String toFileString() {
        return String.join("|",
                id,
                userId,
                roomId,
                checkIn.toString(),
                checkOut.toString(),
                String.valueOf(guests),
                String.format(Locale.US, "%.2f", totalPrice),
                status);
    }

    public static Booking fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 8) {
            throw new IllegalArgumentException("Invalid booking record: " + line);
        }
        return new Booking(p[0], p[1], p[2],
                LocalDate.parse(p[3]), LocalDate.parse(p[4]),
                Integer.parseInt(p[5]), Double.parseDouble(p[6]), p[7]);
    }
}
