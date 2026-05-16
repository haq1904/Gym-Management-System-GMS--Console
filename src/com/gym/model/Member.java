package com.gym.model;

public class Member extends User {

    public Member(String username, String password, String fullName) {
        super(username, password, fullName, "com.gym.model.Member");
    }

    public Member() {
        super();
        this.role = "com.gym.model.Member"; // hard code cho role
    }
    @Override
    public void displayMenu() {

    }
}
