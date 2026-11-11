package com.hotel.model;

public class StandardRoom extends Room {

    public StandardRoom() {
        super();
    }

    public StandardRoom(String id, String roomNumber, double pricePerNight,
                        int capacity, boolean inService, String description) {
        super(id, roomNumber, pricePerNight, capacity, inService, description);
    }

    @Override
    public String getType() {
        return "STANDARD";
    }

    @Override
    public double getServiceChargeRate() {
        return 0.0;
    }

    @Override
    public String getAmenities() {
        return "Air conditioning, Wi-Fi, TV";
    }
}