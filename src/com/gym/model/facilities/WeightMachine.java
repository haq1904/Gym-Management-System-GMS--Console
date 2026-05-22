package com.gym.model.facilities;

public class WeightMachine extends GymMachine {

    private double maxWeight;

    public WeightMachine(String machineId, String machineName, String importDate, String nextMaintenanceDate, String currentStatus, double maxWeight) {
        super(machineId, machineName, importDate, nextMaintenanceDate, currentStatus);
        machineType = "Weight Machine";
        this.maxWeight = maxWeight;
    }

    @Override
    public void displayMachineDetails() {
        System.out.println("--- WEIGHT MACHINE DETAILS ---");
        System.out.println("ID: " + machineId + " | Name: " + machineName + " | Import date: "+ importDate);
        System.out.println("Max Weight: " + maxWeight + " kg");
        System.out.println("Status: " + currentStatus + " | Next Maintenance: " + nextMaintenanceDate);
        System.out.println("------------------------------");
    }

    @Override
    public void performMaintenance() {
        this.currentStatus = "Good";
    }

    // Getter & Setter
    public double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(double maxWeight) { this.maxWeight = maxWeight; }

}
