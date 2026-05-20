package com.gym.manage;

import com.gym.model.schedule.WorkoutSchedule;
import com.gym.repository.IRepository;

import java.util.List;
import java.util.Scanner;

public class WorkoutScheduleManagement {
    private Scanner scanner = new Scanner(System.in);

    // =========================================================================
    // 1. DÀNH CHO TRAINER (HUẤN LUYỆN VIÊN)
    // =========================================================================
    public void displayTrainerMenu(List<WorkoutSchedule> scheduleList, IRepository<WorkoutSchedule> scheduleRepo, String trainerUsername) {
        boolean isManaging = true;

        while (isManaging) {
            System.out.println("\n--- TRAINER SCHEDULE MENU ---");
            System.out.println("1. Create and Assign a new workout schedule");
            System.out.println("2. View all schedules assigned by me");
            System.out.println("0. Back to Main Menu");
            System.out.print("-> Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleCreateSchedule(scheduleList, scheduleRepo, trainerUsername);
                    break;
                case "2":
                    handleViewTrainerSchedules(scheduleList, trainerUsername);
                    break;
                case "0":
                    System.out.println("[ INFO ] Returning...");
                    isManaging = false;
                    break;
                default:
                    System.out.println("[ WARNING ] Invalid option.");
                    break;
            }
        }
    }

    private void handleCreateSchedule(List<WorkoutSchedule> scheduleList, IRepository<WorkoutSchedule> scheduleRepo, String trainerUsername) {
        System.out.println("\n[ CREATE NEW SCHEDULE ]");

        // 1. Tự động sinh mã Lịch tập (Ví dụ: SCH004)
        String newScheduleId = generateScheduleId(scheduleList);
        System.out.println("Auto-generated Schedule ID: " + newScheduleId);

        // 2. Nhập thông tin
        System.out.print("Enter Member's Username to assign: ");
        String memberUsername = scanner.nextLine().trim();

        System.out.print("Enter Date (dd/MM/yyyy): ");
        String date = scanner.nextLine().trim();

        System.out.print("Enter Time (HH:mm): ");
        String time = scanner.nextLine().trim();

        System.out.print("Enter Exercises (separated by '|', e.g., Yoga|Cardio): ");
        String exercises = scanner.nextLine().trim();

        // Mặc định tạo ra thì tiến độ là "Not Started"
        String progressStatus = "Not Started";

        // 3. Tạo đối tượng và lưu
        WorkoutSchedule newSchedule = new WorkoutSchedule(newScheduleId, memberUsername, trainerUsername, date, time, exercises, progressStatus);
        scheduleRepo.add(scheduleList, newSchedule);
        System.out.println("[ SUCCESS ] Schedule assigned to " + memberUsername + " successfully!");
    }

    private void handleViewTrainerSchedules(List<WorkoutSchedule> scheduleList, String trainerUsername) {
        System.out.println("\n--- SCHEDULES ASSIGNED BY " + trainerUsername.toUpperCase() + " ---");
        int count = 0;
        for (WorkoutSchedule s : scheduleList) {
            if (s.getTrainerUsername().equalsIgnoreCase(trainerUsername)) {
                printScheduleInfo(s);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("[ INFO ] You haven't assigned any schedules yet.");
        }
    }

    // =========================================================================
    // 2. DÀNH CHO MEMBER (HỘI VIÊN)
    // =========================================================================
    public void displayMemberMenu(List<WorkoutSchedule> scheduleList, IRepository<WorkoutSchedule> scheduleRepo, String memberUsername) {
        boolean isManaging = true;

        while (isManaging) {
            System.out.println("\n--- MY WORKOUT SCHEDULE MENU ---");
            System.out.println("1. View My Workout Schedules");
            System.out.println("2. Update Schedule Progress");
            System.out.println("0. Back to Main Menu");
            System.out.print("-> Select an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleViewMemberSchedules(scheduleList, memberUsername);
                    break;
                case "2":
                    handleUpdateProgress(scheduleList, scheduleRepo, memberUsername);
                    break;
                case "0":
                    System.out.println("[ INFO ] Returning...");
                    isManaging = false;
                    break;
                default:
                    System.out.println("[ WARNING ] Invalid option.");
                    break;
            }
        }
    }

    private void handleViewMemberSchedules(List<WorkoutSchedule> scheduleList, String memberUsername) {
        System.out.println("\n--- MY SCHEDULES (" + memberUsername + ") ---");
        int count = 0;
        for (WorkoutSchedule s : scheduleList) {
            if (s.getMemberUsername().equalsIgnoreCase(memberUsername)) {
                printScheduleInfo(s);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("[ INFO ] You don't have any workout schedules yet.");
        }
    }

    private void handleUpdateProgress(List<WorkoutSchedule> scheduleList, IRepository<WorkoutSchedule> scheduleRepo, String memberUsername) {
        System.out.println("\n[ UPDATE PROGRESS ]");
        System.out.print("Enter Schedule ID to update (e.g., SCH001): ");
        String targetId = scanner.nextLine().trim();

        WorkoutSchedule targetSchedule = null;

        // Tìm lịch tập, nhưng PHẢI đảm bảo lịch đó là của Member này (Bảo mật chéo)
        for (WorkoutSchedule s : scheduleList) {
            if (s.getScheduleId().equalsIgnoreCase(targetId) && s.getMemberUsername().equalsIgnoreCase(memberUsername)) {
                targetSchedule = s;
                break;
            }
        }

        if (targetSchedule == null) {
            System.out.println("[ ERROR ] Schedule ID not found or it does not belong to you!");
        } else {
            System.out.println("Current Status: " + targetSchedule.getProgressStatus());
            System.out.println("1. Not Started | 2. In Progress | 3. Completed | 4. Absent");
            System.out.print("-> Select new status (1-4): ");
            String statusChoice = scanner.nextLine().trim();

            String newStatus = "";
            if (statusChoice.equals("1")) newStatus = "Not Started";
            else if (statusChoice.equals("2")) newStatus = "In Progress";
            else if (statusChoice.equals("3")) newStatus = "Completed";
            else if (statusChoice.equals("4")) newStatus = "Absent";
            else {
                System.out.println("[ WARNING ] Invalid choice. Update canceled.");
                return;
            }

            targetSchedule.setProgressStatus(newStatus);
            scheduleRepo.saveData(scheduleList); // Lưu thay đổi xuống CSV
            System.out.println("[ SUCCESS ] Progress updated to: " + newStatus);
        }
    }

    // =========================================================================
    // 3. CÁC HÀM HELPER CHUNG (DÙNG NỘI BỘ)
    // =========================================================================
    private void printScheduleInfo(WorkoutSchedule s) {
        System.out.println("----------------------------------------");
        System.out.println("ID       : " + s.getScheduleId());
        System.out.println("Trainer  : " + s.getTrainerUsername());
        System.out.println("Member   : " + s.getMemberUsername());
        System.out.println("Date/Time: " + s.getDate() + " at " + s.getTime());
        // Thay dấu | bằng dấu phẩy và khoảng trắng cho dễ đọc
        System.out.println("Exercises: " + s.getExercises().replace("|", ", "));
        System.out.println("Status   : " + s.getProgressStatus());
    }

    private String generateScheduleId(List<WorkoutSchedule> scheduleList) {
        int maxId = 0;
        for (WorkoutSchedule s : scheduleList) {
            String currentId = s.getScheduleId();
            if (currentId.toUpperCase().startsWith("SCH")) {
                try {
                    int number = Integer.parseInt(currentId.substring(3));
                    if (number > maxId) {
                        maxId = number;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu format sai
                }
            }
        }
        return String.format("SCH%03d", maxId + 1);
    }
}
