package com.gym.model.users;

import com.gym.repository.GymContext;
import com.gym.repository.UserRepository;
import com.gym.view.IDisplayMenu;

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
        return null;
    }

    //Xep lich cho member
    public void assignWorkoutSchedule(Member member, String date, String exercise) {
        // Code tạo đối tượng Schedule và lưu xuống file schedules.csv
    }

    //Diem danh member
    public void trackAttendance(Member member, String date, boolean isPresent) {
        // Code lưu trạng thái đi tập xuống file attendance.csv
    }

    //Theo doi tien do cua member
    public void viewMemberProgress(Member member) {
        System.out.println("Tiến độ của " + member.getFullName() + ": " + member.getWorkoutProgress()); //
    }

}
