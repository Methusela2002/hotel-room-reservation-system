package com.hotel.model;

import java.util.Locale;

public abstract class Room {

    private String id;
    private String roomNumber;
    private double pricePerNight;
    private int capacity;
    private boolean inService;   // false = under maintenance, cannot be booked
    private String description;

    public Room() {
    }

    public Room(String id, String roomNumber, double pricePerNight,
                int capacity, boolean inService, String description) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.inService = inService;
        this.description = description;
    }

    // Polymorphic methods: each room type provides its own version
    public abstract String getType();

    public abstract double getServiceChargeRate();

    public abstract String getAmenities();

    // Total cost for a stay, including the service charge of this room type
    public double calculateTotalPrice(int nights) {
        double total = pricePerNight * nights * (1 + getServiceChargeRate());
        return Math.round(total * 100.0) / 100.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isInService() {
        return inService;
    }

    public void setInService(boolean inService) {
        this.inService = inService;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // One line for rooms.txt
    // Format: id|type|roomNumber|pricePerNight|capacity|inService|description
    public String toFileString() {
        return String.join("|",
                id,
                getType(),
                roomNumber,
                String.format(Locale.US, "%.2f", pricePerNight),
                String.valueOf(capacity),
                String.valueOf(inService),
                description == null ? "" : description);
    }

    // Rebuilds the correct subclass from one line of rooms.txt
    public static Room fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 7) {
            throw new IllegalArgumentException("Invalid room record: " + line);
        }
        String id = p[0];
        String type = p[1];
        String roomNumber = p[2];
        double price = Double.parseDouble(p[3]);
        int capacity = Integer.parseInt(p[4]);
        boolean inService = Boolean.parseBoolean(p[5]);
        String description = p[6];

        if ("STANDARD".equals(type)) {
            return new StandardRoom(id, roomNumber, price, capacity, inService, description);
        } else if ("DELUXE".equals(type)) {
            return new DeluxeRoom(id, roomNumber, price, capacity, inService, description);
        } else if ("SUITE".equals(type)) {
            return new SuiteRoom(id, roomNumber, price, capacity, inService, description);
        }
        throw new IllegalArgumentException("Unknown room type: " + type);
    }
}