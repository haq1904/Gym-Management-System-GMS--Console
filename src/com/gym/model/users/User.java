package com.gym.model.users;

import com.gym.repository.GymContext;
import com.gym.repository.UserRepository;
import com.gym.view.AdminView;
import com.gym.view.IDisplayMenu;

import java.util.List;

public abstract class User {


    protected String username;
    protected String password;
    protected String fullName;
    protected String role;



    protected IDisplayMenu menu;

    protected User(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
    protected User(){
        this.username = "";
        this.password = "";
        this.fullName = "";
        this.role = "";
    }

    //Phuong thuc kiem tra mat khau dung chung
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }



    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassword() {
        return password;
    }

    public abstract IDisplayMenu getMenu();


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
