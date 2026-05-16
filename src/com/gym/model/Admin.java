package com.gym.model;

public class Admin extends User {
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "com.gym.model.Admin");
    }

    public Admin() {
        super();
        this.role = "com.gym.model.Admin"; // hard code cho role
    }

    @Override
    public void displayMenu() {

    }
}