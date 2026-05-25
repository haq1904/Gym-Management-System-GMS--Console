package com.gym.view;

import com.gym.manage.*;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import java.util.Scanner;

public class AdminView implements IDisplayMenu {

    private MemberShipManagement memberShipManagement;
    private AttendanceTracking attendanceTracking;
    private Scanner scanner = new Scanner(System.in);
    private User loggedInAdmin;

    @Override
    public void displayMenu(GymContext context, User loggedInAdmin) {
        memberShipManagement = new MemberShipManagement(context);
        attendanceTracking = new AttendanceTracking(context);
        this.loggedInAdmin = loggedInAdmin;

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=========================================");
            System.out.println("          ADMINISTRATOR MENU             ");
            System.out.println("          Welcome: " + loggedInAdmin.getFullName());
            System.out.println("=========================================");
            System.out.println("1. Manage Facilities, Trainers, and Plans");
            System.out.println("2. Manage Member (Add, Update, Delete)");
            System.out.println("3. View Reports (Revenue & Attendance)");
            System.out.println("0. Logout");
            System.out.println("=========================================");
            System.out.print("-> Select an option (0-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n[ FEATURE ] Opening Facilities & Trainers Management...");
                    break;
                case "2":
                    System.out.println("\nOpening Member Management...");
                    displayMemberManagementMenu();
                    break;
                case "3":
                    System.out.println("\nOpening Reports Menu...");
                    displayViewReportMenu();
                    break;
                case "0":
                    System.out.println("\n[ LOGOUT ] Returning to the main screen...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("\n[ WARNING ] Invalid choice. Please enter a number from 0 to 3!");
                    break;
            }

            if (isRunning && !choice.equals("2") && !choice.equals("3")) {
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void displayViewReportMenu() {
        boolean isReporting = true;
        while (isReporting) {
            System.out.println("\n--- REPORT MANAGEMENT MENU ---");
            System.out.println("1. Total Revenue from Subscriptions");
            System.out.println("2. Attendance Reports");
            System.out.println("0. Back to Main Admin Menu");
            System.out.print("-> Select an option (0-2): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    attendanceTracking.handleViewRevenueAndMembership();

                    break;

                case "2":
                    // Gọi tiếp menu con hiển thị các tiêu chí lọc của Attendance
                    displayAttendanceReportSubMenu();
                    break;

                case "0":
                    System.out.println("[ INFO ] Returning to Main Admin Menu...");
                    isReporting = false;
                    break;

                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter 0, 1, or 2.");
                    break;
            }
        }
    }

    private void displayAttendanceReportSubMenu() {
        boolean isAttendanceReporting = true;
        while (isAttendanceReporting) {
            System.out.println("\n--- GYM ATTENDANCE REPORTS ---");
            System.out.println("1. View Attendance Summaries");
            System.out.println("2. View All Attendance History ");
            System.out.println("3. Filter by Specific Date");
            System.out.println("4. Filter by Member Name / Username");
            System.out.println("5. Filter by Trainer Name / Username");
            System.out.println("0. Back to Reports Menu");
            System.out.print("-> Select an option (0-4): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    attendanceTracking.handleViewAttendanceSummary(loggedInAdmin.getUsername(), true); // Gọi hàm xử lý cốt lõib
                    break;
                case "2":
                    attendanceTracking.handleViewAllHistory(loggedInAdmin.getFullName(), true); // Gọi hàm xử lý cốt lõi
                    break;
                case "3":
                    attendanceTracking.handleFilterByDate(loggedInAdmin.getFullName(), true); // Gọi hàm xử lý cốt lõi
                    break;
                case "4":
                    attendanceTracking.handleFilterByMember(loggedInAdmin.getFullName(), true); // Gọi hàm xử lý cốt lõi
                    break;
                case "5":
                    attendanceTracking.handleFilterByTrainer(loggedInAdmin.getFullName(), true); // Gọi hàm xử lý cốt lõi
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

    private void displayMemberManagementMenu() {
        boolean isManaging = true;
        while (isManaging) {
            System.out.println("\n--- MEMBER MANAGEMENT MENU ---");
            System.out.println("1. Add a new Member");
            System.out.println("2. Update Profile");
            System.out.println("3. Delete a Member");
            System.out.println("4. View Member Information");
            System.out.println("0. Back to Main Admin Menu");
            System.out.print("-> Select an option (0-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    memberShipManagement.handleAddMember();
                    break;
                case "2":
                    memberShipManagement.handleUpdateProfileMember(null, true);
                    break;
                case "3":
                    memberShipManagement.handleDeleteMember();
                    break;
                case "4":
                    memberShipManagement.handleViewMemberInfo();
                    break;
                case "0":

                    System.out.println("[ INFO ] Returning to Main Admin Menu...");
                    isManaging = false;
                    break;
                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter from 0 to 4.");
                    break;
            }
        }
    }
}
