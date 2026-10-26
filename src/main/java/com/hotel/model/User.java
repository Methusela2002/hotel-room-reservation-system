package com.hotel.model;

public abstract class User {

    private String id;
    private String fullName;
    private String email;
    private String password;
    private String phone;

    public User() {
    }

    public User(String id, String fullName, String email, String password, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    // Polymorphic methods: each subclass provides its own version
    public abstract String getRole();

    public abstract String getHomePage();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Converts this object into one line of text for users.txt
    // Format: id|role|fullName|email|password|phone
    public String toFileString() {
        return String.join("|", id, getRole(), fullName, email, password, phone);
    }

    // Rebuilds the correct subclass from one line of users.txt
    public static User fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 6) {
            throw new IllegalArgumentException("Invalid user record: " + line);
        }
        if ("ADMIN".equals(p[1])) {
            return new Admin(p[0], p[2], p[3], p[4], p[5]);
        }
        return new Customer(p[0], p[2], p[3], p[4], p[5]);
    }
}