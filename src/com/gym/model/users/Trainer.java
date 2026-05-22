package com.gym.model.users;

import com.gym.repository.GymContext;
import com.gym.repository.UserRepository;
import com.gym.view.IDisplayMenu;
import com.gym.view.TrainerView;

import java.util.List;

public class Trainer extends User {

    public String getSpecialty() {
        return specialty;
    }

    private String specialty; //Chuyen mon

    public Trainer(String username, String password, String fullName, String specialty) {
        super(username, password, fullName, "Trainer");
        this.specialty = specialty;
    }

    public Trainer() {
        super();
        this.role = "Trainer"; // hard code cho role
    }

    @Override
    public IDisplayMenu getMenu() {
        return new TrainerView();
    }




}
