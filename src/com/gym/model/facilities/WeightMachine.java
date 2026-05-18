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
        // Bảo trì máy tạ là kiểm tra dây cáp và ròng rọc
        System.out.println("[ MAINTENANCE ] Inspecting cables, pulleys, and lubricating weight stacks for: " + machineName);
        this.currentStatus = "Good";
        System.out.println("[ SUCCESS ] Maintenance completed. Status updated to 'Good'.");
    }

    // Getter & Setter
    public double getMaxWeight() { return maxWeight; }
    public void setMaxWeight(double maxWeight) { this.maxWeight = maxWeight; }

    @Override
    public String toString() {
        return "WeightMachine{" +
                "maxWeight=" + maxWeight +
                ", machineId='" + machineId + '\'' +
                ", machineName='" + machineName + '\'' +
                ", importDate='" + importDate + '\'' +
                ", nextMaintenanceDate='" + nextMaintenanceDate + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", machineType='" + machineType + '\'' +
                '}';
    }
}
