package com.gym.view;

import com.gym.manage.AttendanceTracking;
import com.gym.manage.WorkoutScheduleManagement;
import com.gym.model.users.Trainer;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import java.util.Scanner;

public class TrainerView implements IDisplayMenu {
    private Scanner scanner = new Scanner(System.in);
    private WorkoutScheduleManagement scheduleManager;
    private AttendanceTracking attendanceTracking;
    private Trainer loggedInTrainer;

    @Override
    public void displayMenu(GymContext context, User loggedInTrainer) {
        scheduleManager = new WorkoutScheduleManagement(context);
        attendanceTracking = new AttendanceTracking(context);
        this.loggedInTrainer = (Trainer) loggedInTrainer;

        boolean isRunning = true;


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
                    displayAttendanceReportSubMenu();
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

    private void displayAttendanceReportSubMenu(){
        boolean isAttendanceReporting = true;
        while (isAttendanceReporting) {
            System.out.println("\n--- GYM ATTENDANCE REPORTS ---");
            System.out.println("1. View Attendance Summaries");
            System.out.println("2. View All Attendance History ");
            System.out.println("3. Filter by Specific Date (Lọc theo Ngày)");
            System.out.println("4. Filter by Member Name / Username");
            System.out.println("0. Back to Reports Menu");
            System.out.print("-> Select an option (0-4): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    attendanceTracking.handleViewAttendanceSummary(loggedInTrainer.getUsername(),false);
                    break;
                case "2":
                    attendanceTracking.handleViewAllHistory(loggedInTrainer.getUsername(),false);
                    break;
                case "3":
                    attendanceTracking.handleFilterByDate(loggedInTrainer.getFullName(),false);
                    break;
                case "4":
                    attendanceTracking.handleFilterByMember(loggedInTrainer.getFullName(),false);
                    break;
                case "0":
                    System.out.println("[ INFO ] Returning to Reports Menu...");
                    isAttendanceReporting = false;
                    break;
                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter from 0 to 4.");
                    break;
            }
        }
    }
}