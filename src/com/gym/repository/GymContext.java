package com.gym.repository;

import com.gym.model.users.User;
import com.gym.model.facilities.GymMachine;
import java.util.List;

public class GymContext {
    private IRepository<User> userRepo;
    private IRepository<GymMachine> machineRepo;
    private List<User> userList;
    private List<GymMachine> machineList;

    // Constructor gom tất cả vào
    public GymContext(IRepository<User> userRepo, IRepository<GymMachine> machineRepo) {
        this.userRepo = userRepo;
        this.machineRepo = machineRepo;
        this.userList = userRepo.loadData();
        this.machineList = machineRepo.loadData();
    }

    // Các hàm Getters để lấy đồ nghề ra
    public IRepository<User>  getUserRepo() { return userRepo; }
    public IRepository<GymMachine> getMachineRepo() { return machineRepo; }
    public List<User> getUserList() { return userList; }
    public List<GymMachine> getMachineList() { return machineList; }
}
