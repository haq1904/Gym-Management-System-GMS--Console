package com.gym.repository;

import com.gym.model.users.User;
import com.gym.model.facilities.GymMachine;
import java.util.List;

public class GymContext {
    private UserRepository userRepo;
    private MachineRepository machineRepo;
    private List<User> userList;
    private List<GymMachine> machineList;

    // Constructor gom tất cả vào
    public GymContext(UserRepository userRepo,List<User> userList, MachineRepository machineRepo,
                       List<GymMachine> machineList) {
        this.userRepo = userRepo;
        this.machineRepo = machineRepo;
        this.userList = userList;
        this.machineList = machineList;
    }

    // Các hàm Getters để lấy đồ nghề ra
    public UserRepository getUserRepo() { return userRepo; }
    public MachineRepository getMachineRepo() { return machineRepo; }
    public List<User> getUserList() { return userList; }
    public List<GymMachine> getMachineList() { return machineList; }
}
