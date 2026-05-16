package com.gym.model;

public class Trainer extends User {

    public Trainer(String username, String password, String fullName) {
        super(username, password, fullName, "com.gym.model.Trainer");
    }

    public Trainer() {
        super();
        this.role = "com.gym.model.Trainer"; // hard code cho role
    }
    @Override
    public void displayMenu() {

    }
}
