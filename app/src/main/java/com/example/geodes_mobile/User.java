package com.example.geodes_mobile;

public class User {
    public String firstName;
    public String lastName;
    public String email;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
