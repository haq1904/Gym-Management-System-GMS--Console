package com.gym.model.facilities;

public abstract class GymMachine {

    protected String machineId;           // ID của máy (VD: M01, C02)
    protected String machineName;         // Tên máy (VD: Treadmill, Leg Press)
    protected String importDate;          // Ngày nhập máy (VD: 15/05/2023)
    protected String nextMaintenanceDate; // Thời hạn bảo trì tiếp theo (VD: 15/11/2026)
    protected String currentStatus;       // Tình trạng hiện tại (VD: Good, Needs Repair, Broken)
    protected String machineType;


    public GymMachine(String machineId, String machineName, String importDate, String nextMaintenanceDate, String currentStatus) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.importDate = importDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.currentStatus = currentStatus;
    }

    public abstract void displayMachineDetails();

    public abstract void performMaintenance();

    public void updateStatus(String newStatus) {
        this.currentStatus = newStatus;
        System.out.println("[ SUCCESS ] Machine " + this.machineName + " status updated to: " + newStatus);
    }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }

    public String getMachineName() { return machineName; }
    public void setMachineName(String machineName) { this.machineName = machineName; }

    public String getImportDate() { return importDate; }
    public void setImportDate(String importDate) { this.importDate = importDate; }

    public String getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(String nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
}
