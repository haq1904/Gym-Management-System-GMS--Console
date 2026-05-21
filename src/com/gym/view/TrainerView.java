package com.gym.view;

import com.gym.manage.WorkoutScheduleManagement;
import com.gym.model.schedule.WorkoutSchedule;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;

import java.util.List;
import java.util.Scanner;

public class TrainerView implements IDisplayMenu {
    private Scanner scanner = new Scanner(System.in);

    // Khởi tạo chuyên viên xử lý logic lịch tập


    @Override
    public void displayMenu(GymContext context, User loggedInTrainer) {
        WorkoutScheduleManagement scheduleManager = new WorkoutScheduleManagement(context);
        boolean isRunning = true;

        // Lấy danh sách và kho chứa từ Context (Nhớ khai báo thêm trong GymContext nha)
        List<WorkoutSchedule> scheduleList = context.getSchedulesList();
        IRepository<WorkoutSchedule> scheduleRepo = context.getScheduleRepo();

        while (isRunning) {
            System.out.println("\n=========================================");
            System.out.println("              TRAINER MENU               ");
            System.out.println("         Welcome: " + loggedInTrainer.getFullName());
            System.out.println("=========================================");
            System.out.println("1. Create and Assign a workout schedule");
            System.out.println("2. View all schedules assigned by me");
            System.out.println("3. Track member attendance and progress");
            System.out.println("0. Logout");
            System.out.println("=========================================");
            System.out.print("-> Select an option (0-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    scheduleManager.createAndAssignSchedule(loggedInTrainer.getUsername());
                    break;

                case "2":
                    scheduleManager.displaySchedules(loggedInTrainer.getUsername(),true);
                    break;

                case "3":
                    System.out.println("\n[ FEATURE ] Track member attendance and progress is under construction...");
                    scheduleManager.updateProgress(loggedInTrainer.getUsername(),true);
                    break;

                case "0":
                    System.out.println("\n[ LOGOUT ] Returning to the main screen...");
                    isRunning = false;
                    break;

                default:
                    System.out.println("\n[ WARNING ] Invalid choice. Please enter a valid option!");
                    break;
            }
        }
    }
}