package com.gym.model.schedule;

public class WorkoutSchedule {
    private String scheduleId;
    private String memberUsername;
    private String trainerUsername;
    private String date; // Định dạng dd/MM/yyyy
    private String time; // Định dạng HH:mm (MỚI THÊM)
    private String exercises; // Các bài tập cách nhau bởi dấu |
    private String progressStatus; // Not Started, In Progress, Completed, Absent

    public WorkoutSchedule(String scheduleId, String memberUsername, String trainerUsername, String date, String time, String exercises, String progressStatus) {
        this.scheduleId = scheduleId;
        this.memberUsername = memberUsername;
        this.trainerUsername = trainerUsername;
        this.date = date;
        this.time = time; // Gán giờ tập
        this.exercises = exercises;
        this.progressStatus = progressStatus;
    }

    // --- GETTERS ---
    public String getScheduleId() { return scheduleId; }
    public String getMemberUsername() { return memberUsername; }
    public String getTrainerUsername() { return trainerUsername; }
    public String getDate() { return date; }
    public String getTime() { return time; } // MỚI THÊM
    public String getExercises() { return exercises; }
    public String getProgressStatus() { return progressStatus; }

    // --- SETTERS ---
    public void setProgressStatus(String progressStatus) {
        this.progressStatus = progressStatus;
    }
    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

}