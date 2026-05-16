package com.gym.model;

public abstract class User {


    protected String username;
    protected String password;
    protected String fullName;
    protected String role;

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

    //Cac user deu can phai override lai
    public abstract void displayMenu();

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }


}
