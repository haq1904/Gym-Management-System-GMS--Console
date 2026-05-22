package com.gym.model.facilities;

public class FreeWeight extends GymMachine {

    private String material;

    public FreeWeight(String machineId, String machineName, String importDate, String nextMaintenanceDate, String currentStatus, String material) {
        super(machineId, machineName, importDate, nextMaintenanceDate, currentStatus);
        machineType = "Free Weight";
        this.material = material;
    }

    @Override
    public void displayMachineDetails() {
        System.out.println("--- FREE WEIGHT DETAILS ---");
        System.out.println("ID: " + machineId + " | Name: " + machineName);
        System.out.println("Material: " + material);
        System.out.println("Status: " + currentStatus + " | Next Inspection: " + nextMaintenanceDate);
        System.out.println("---------------------------");
    }

    @Override
    public void performMaintenance() {
        this.currentStatus = "Good";
    }

    // Getter & Setter
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

}
