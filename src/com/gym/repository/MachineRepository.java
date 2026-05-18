package com.gym.repository;

import com.gym.model.facilities.*; // Đảm bảo import đúng đường dẫn package của ông
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MachineRepository implements IRepository<GymMachine> {

    private String filePath;

    // Constructor nhận đường dẫn file
    public MachineRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<GymMachine> loadData() {
        List<GymMachine> machineList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");

                // Đảm bảo file có đúng 7 cột như cấu trúc
                if (data.length == 7) {
                    String id = data[0].trim();
                    String name = data[1].trim();
                    String importDate = data[2].trim();
                    String maintDate = data[3].trim();
                    String status = data[4].trim();
                    String machineType = data[5].trim();
                    String specificAttribute = data[6].trim();

                    // Khởi tạo đối tượng dựa trên machineType
                    switch (machineType) {
                        case "WeightMachine":
                            double maxWeight = Double.parseDouble(specificAttribute);
                            machineList.add(new WeightMachine(id, name, importDate, maintDate, status, maxWeight));
                            break;
                        case "FreeWeight":
                            machineList.add(new FreeWeight(id, name, importDate, maintDate, status, specificAttribute));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ ERROR ] Cannot read facilities data: " + e.getMessage());
        }

        return machineList;
    }

    /**
     * HÀM 2: GHI ĐÈ DANH SÁCH XUỐNG FILE CSV
     */
    @Override
    public void saveData(List<GymMachine> dataList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath))) {
            for (GymMachine machine : dataList) {
                StringBuilder sb = new StringBuilder();

                // Nối 5 cột thuộc tính chung
                sb.append(machine.getMachineId()).append(",")
                        .append(machine.getMachineName()).append(",")
                        .append(machine.getImportDate()).append(",")
                        .append(machine.getNextMaintenanceDate()).append(",")
                        .append(machine.getCurrentStatus()).append(",");

                if (machine instanceof WeightMachine) {
                    WeightMachine wm = (WeightMachine) machine;
                    sb.append("Weigh tMachine,").append(wm.getMaxWeight());
                }
                else if (machine instanceof FreeWeight) {
                    FreeWeight fw = (FreeWeight) machine;
                    sb.append("Free Weight,").append(fw.getMaterial());
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ ERROR ] Cannot save facilities data: " + e.getMessage());
        }
    }

    /**
     * HÀM 3: THÊM MÁY MỚI
     */
    @Override
    public boolean add(List<GymMachine> dataList, GymMachine newMachine) {
        // Kiểm tra trùng lặp ID máy (machineId)
        for (GymMachine m : dataList) {
            if (m.getMachineId().equalsIgnoreCase(newMachine.getMachineId())) {
                System.out.println("[ ERROR ] Machine ID '" + newMachine.getMachineId() + "' already exists!");
                return false;
            }
        }

        dataList.add(newMachine);
        saveData(dataList); // Lưu lại ngay lập tức
        System.out.println("[ SUCCESS ] Added machine: " + newMachine.getMachineName());
        return true;
    }

    /**
     * HÀM 4: XÓA MÁY THEO ID
     */
    @Override
    public boolean delete(List<GymMachine> dataList, String machineId) {
        GymMachine machineToDelete = null;

        // Tìm kiếm máy móc theo ID
        for (GymMachine m : dataList) {
            if (m.getMachineId().equalsIgnoreCase(machineId)) {
                machineToDelete = m;
                break;
            }
        }

        // Thực hiện xóa
        if (machineToDelete != null) {
            dataList.remove(machineToDelete);
            saveData(dataList);
            System.out.println("[ SUCCESS ] Deleted machine ID: " + machineId);
            return true;
        } else {
            System.out.println("[ ERROR ] Machine ID '" + machineId + "' not found.");
            return false;
        }
    }
}