package com.hotel.model;

public class Customer extends User {

    public Customer() {
        super();
    }

    public Customer(String id, String fullName, String email, String password, String phone) {
        super(id, fullName, email, password, phone);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public String getHomePage() {
        return "customer-home.jsp";
    }
}