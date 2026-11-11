package com.hotel.model;

public class SuiteRoom extends Room {

    public SuiteRoom() {
        super();
    }

    public SuiteRoom(String id, String roomNumber, double pricePerNight,
                     int capacity, boolean inService, String description) {
        super(id, roomNumber, pricePerNight, capacity, inService, description);
    }

    @Override
    public String getType() {
        return "SUITE";
    }

    @Override
    public double getServiceChargeRate() {
        return 0.10;
    }

    @Override
    public String getAmenities() {
        return "Air conditioning, Wi-Fi, TV, Mini bar, Balcony, Living area, Breakfast";
    }
}