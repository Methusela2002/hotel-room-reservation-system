package com.hotel.model;

public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(String id, String fullName, String email, String password, String phone) {
        super(id, fullName, email, password, phone);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getHomePage() {
        return "admin-dashboard.jsp";
    }
}