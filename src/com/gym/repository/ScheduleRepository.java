package com.gym.repository;

import com.gym.model.schedule.WorkoutSchedule; // Import đúng Model của ông nhé

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository implements IRepository<WorkoutSchedule> {

    private String filePath;

    public ScheduleRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<WorkoutSchedule> loadData() {
        List<WorkoutSchedule> scheduleList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");

                // Đảm bảo file có đúng 7 cột (ĐÃ CẬP NHẬT)
                if (data.length == 7) {
                    String scheduleId = data[0].trim();
                    String memberUsername = data[1].trim();
                    String trainerUsername = data[2].trim();
                    String date = data[3].trim();
                    String time = data[4].trim(); // Lấy Giờ tập ra
                    String exercises = data[5].trim();
                    String progressStatus = data[6].trim();

                    scheduleList.add(new WorkoutSchedule(
                            scheduleId, memberUsername, trainerUsername, date, time, exercises, progressStatus
                    ));
                } else {
                    System.out.println("[ WARNING ] Skipping malformed line in schedules.csv: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("[ ERROR ] Cannot read schedules data: " + e.getMessage());
        }

        return scheduleList;
    }

    @Override
    public void saveData(List<WorkoutSchedule> dataList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath))) {
            for (WorkoutSchedule schedule : dataList) {
                StringBuilder sb = new StringBuilder();

                // Nối 7 cột lại bằng dấu phẩy
                sb.append(schedule.getScheduleId()).append(",")
                        .append(schedule.getMemberUsername()).append(",")
                        .append(schedule.getTrainerUsername()).append(",")
                        .append(schedule.getDate()).append(",")
                        .append(schedule.getTime()).append(",") // Ghi Giờ tập vào file
                        .append(schedule.getExercises()).append(",")
                        .append(schedule.getProgressStatus());

                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ ERROR ] Cannot save schedules data: " + e.getMessage());
        }
    }

    @Override
    public boolean add(List<WorkoutSchedule> dataList, WorkoutSchedule newSchedule) {
        for (WorkoutSchedule s : dataList) {
            if (s.getScheduleId().equalsIgnoreCase(newSchedule.getScheduleId())) {
                System.out.println("[ ERROR ] Schedule ID '" + newSchedule.getScheduleId() + "' already exists!");
                return false;
            }
        }

        dataList.add(newSchedule);
        saveData(dataList);
        return true;
    }

    @Override
    public boolean delete(List<WorkoutSchedule> dataList, String scheduleId) {
        WorkoutSchedule scheduleToDelete = null;

        for (WorkoutSchedule s : dataList) {
            if (s.getScheduleId().equalsIgnoreCase(scheduleId)) {
                scheduleToDelete = s;
                break;
            }
        }

        if (scheduleToDelete != null) {
            dataList.remove(scheduleToDelete);
            saveData(dataList);
            System.out.println("[ SUCCESS ] Deleted schedule ID: " + scheduleId);
            return true;
        } else {
            System.out.println("[ ERROR ] Schedule ID '" + scheduleId + "' not found.");
            return false;
        }
    }
}