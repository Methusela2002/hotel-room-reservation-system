package com.hotel.model;

public class DeluxeRoom extends Room {

    public DeluxeRoom() {
        super();
    }

    public DeluxeRoom(String id, String roomNumber, double pricePerNight,
                      int capacity, boolean inService, String description) {
        super(id, roomNumber, pricePerNight, capacity, inService, description);
    }

    @Override
    public String getType() {
        return "DELUXE";
    }

    @Override
    public double getServiceChargeRate() {
        return 0.05;
    }

    @Override
    public String getAmenities() {
        return "Air conditioning, Wi-Fi, TV, Mini bar, Balcony";
    }
}