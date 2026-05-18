package com.gym.model.users;

import com.gym.model.facilities.GymMachine;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;
import com.gym.repository.MachineRepository;
import com.gym.repository.UserRepository;
import com.gym.view.AdminView;
import com.gym.view.IDisplayMenu;

import java.util.List;
import java.util.Scanner;
public class Admin extends User {
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "Admin");
    }

    public Admin() {
        super();
        this.role = "Admin"; // hard code cho role
    }

    @Override
    public IDisplayMenu getMenu() {
        return new AdminView();
    }

}