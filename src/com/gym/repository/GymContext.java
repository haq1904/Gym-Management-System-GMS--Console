package com.gym.repository;

import com.gym.model.schedule.WorkoutSchedule;
import com.gym.model.users.User;
import com.gym.model.facilities.GymMachine;

import java.lang.reflect.WildcardType;
import java.util.List;

public class GymContext {
    private IRepository<User> userRepo;
    private List<User> userList;
    private IRepository<GymMachine> machineRepo;
    private List<GymMachine> machineList;
    private IRepository<WorkoutSchedule> scheduleRepo;
    private List<WorkoutSchedule> schedulesList;




    // Constructor gom tất cả vào
    public GymContext(IRepository<User> userRepo, IRepository<GymMachine> machineRepo, IRepository<WorkoutSchedule> scheduleRepo) {
        this.userRepo = userRepo;
        this.userList = userRepo.loadData();
        this.machineRepo = machineRepo;
        this.machineList = machineRepo.loadData();
        this.scheduleRepo = scheduleRepo;
        this.schedulesList = scheduleRepo.loadData();

    }


    public IRepository<User> getUserRepo() {return userRepo;}

    public List<User> getUserList() {return userList;}

    public IRepository<GymMachine> getMachineRepo() {return machineRepo;}

    public List<GymMachine> getMachineList() {return machineList;}

    public IRepository<WorkoutSchedule> getScheduleRepo() {return scheduleRepo;}

    public List<WorkoutSchedule> getSchedulesList() {return schedulesList;}
}
