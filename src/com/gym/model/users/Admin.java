package com.gym.model.users;

import com.gym.model.facilities.GymMachine;
import com.gym.repository.GymContext;
import com.gym.repository.MachineRepository;
import com.gym.repository.UserRepository;

import java.util.List;
import java.util.Scanner;
public class Admin extends User {
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "Admin");
    }

    public Admin() {
        super();
        this.role = "Admin"; // hard code cho role
    }

    @Override
    public void displayMenu(GymContext context) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        //Lay list user và machine
        List<User> userList = context.getUserList();
        UserRepository userRepo = context.getUserRepo();
        List<GymMachine> machineList = context.getMachineList();
        MachineRepository machineRepo = context.getMachineRepo();


        while (isRunning) {
            // You can call clearScreen() here to clean the Console if needed
            System.out.println("\n=========================================");
            System.out.println("          ADMINISTRATOR MENU             ");
            System.out.println("          Welcome: " + this.getFullName());
            System.out.println("=========================================");
            System.out.println("1. Manage Facilities, Trainers, and Plans");
            System.out.println("2. Manage Members (Add, Update, Delete)");
            System.out.println("3. View Reports (Revenue & Attendance)");
            System.out.println("0. Logout");
            System.out.println("=========================================");
            System.out.print("-> Select an option (0-3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n[ FEATURE ] Opening Facilities & Trainers Management...");
                    // Call manageFacilitiesAndTrainers() here
                    break;
                case "2":
                    System.out.println("\n[ FEATURE ] Opening Member Management...");
                    displayMemberManagementMenu(userList, userRepo);
                    break;
                case "3":
                    System.out.println("\n[ FEATURE ] Generating statistical reports...");
                    // Call generateReports() here
                    break;
                case "0":
                    System.out.println("\n[ LOGOUT ] Returning to the main screen...");
                    isRunning = false; // Break the loop, exit displayMenu()
                    break;
                default:
                    System.out.println("\n[ WARNING ] Invalid choice. Please enter a number from 0 to 3!");
                    break;
            }

            // Pause the screen so the Admin can read the message before the menu re-renders
            if (isRunning) {
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    public void displayMemberManagementMenu(List<User> userList, UserRepository userRepo) {
        Scanner scanner = new Scanner(System.in);
        boolean isManaging = true;

        while (isManaging) {
            System.out.println("\n--- USER MANAGEMENT MENU ---");
            System.out.println("1. Add a new user (Admin/Trainer/Member)");
            System.out.println("2. Delete a user");
            System.out.println("0. Back to Main Admin Menu");
            System.out.print("-> Select an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n[ ADD NEW USER ]");
                    System.out.println("Select Role: 1. Admin | 2. Trainer | 3. Member");
                    System.out.print("-> Choice: ");
                    String roleChoice = scanner.nextLine();

                    String newUsername = "";
                    while (true) {
                        System.out.print("Enter Username: ");
                        newUsername = scanner.nextLine();

                        // Sử dụng Regex: ^[a-zA-Z0-9]+$
                        // Nghĩa là: Từ đầu (^) đến cuối ($) chỉ được phép chứa chữ cái không dấu (a-z, A-Z) và số (0-9)
                        if (newUsername.matches("^[a-zA-Z0-9]+$")) {
                            break; // Dữ liệu chuẩn xác -> Phá vỡ vòng lặp để đi tiếp
                        } else {
                            System.out.println("[ ERROR ] Invalid Username!");
                            System.out.println("Please use ONLY unaccented letters and numbers. No spaces or special characters allowed.");
                        }
                    }
                    System.out.print("Enter Password: ");
                    String newPassword = scanner.nextLine();
                    System.out.print("Enter Full Name: ");
                    String newFullName = scanner.nextLine();

                    User newUser = null;

                    // Xử lý tạo Object dựa trên Role
                    if (roleChoice.equals("1")) {
                        newUser = new Admin(newUsername, newPassword, newFullName);
                    }
                    else if (roleChoice.equals("2")) {
                        newUser = createNewTrainer(newUsername, newPassword, newFullName,scanner);
                    }
                    else if (roleChoice.equals("3")) {
                        newUser = createNewMember(newUsername, newPassword, newFullName,scanner);
                    }
                    else {
                        System.out.println("[ WARNING ] Invalid role selected! Action canceled.");
                        break;
                    }

                    // Gọi UserRepository để lưu
                    userRepo.add(userList, newUser);
                    break;

                case "2":
                    System.out.println("\n[ DELETE USER ]");
                    System.out.print("Enter the Username to delete: ");
                    String usernameToDelete = scanner.nextLine();

                    // LOGIC CHẶN ADMIN TỰ XÓA CHÍNH MÌNH
                    if (usernameToDelete.equals(this.username)) {
                        System.out.println("[ ERROR ] Security Alert: You cannot delete your own Admin account!");
                        System.out.println("Please ask another Administrator to perform this action.");
                    } else {
                        // Nếu không phải tự xóa, tiến hành gọi Repository để xóa
                        userRepo.delete(userList, usernameToDelete);
                    }
                    break;

                case "0":
                    System.out.println("[ INFO ] Returning to Main Menu...");
                    isManaging = false;
                    break;

                default:
                    System.out.println("[ WARNING ] Invalid option. Please try again.");
                    break;
            }
        }
    }

    private Member createNewMember(String username, String password, String fullName, Scanner scanner) {
        String type = "";
        String status = "";

        // Chọn Gói tập
        while (true) {
            System.out.println("\n[ MEMBERSHIP TYPE ]");
            System.out.println("1. Trial (1 Month)");
            System.out.println("2. Newbie (3 Months)");
            System.out.println("3. VIP (6 Months)");
            System.out.println("4. Premium (12 Months)");
            System.out.print("-> Select Membership Type (1-4): ");
            String typeChoice = scanner.nextLine();

            if (typeChoice.equals("1")) { type = "Trial (1 Month)"; break; }
            else if (typeChoice.equals("2")) { type = "Newbie (3 Months)"; break; }
            else if (typeChoice.equals("3")) { type = "VIP (6 Months)"; break; }
            else if (typeChoice.equals("4")) { type = "Premium (12 Months)"; break; }
            else {
                System.out.println("[ WARNING ] Invalid choice! Please select from 1 to 4.");
            }
        }

        // Chọn Trạng thái
        while (true) {
            System.out.println("\n[ MEMBERSHIP STATUS ]");
            System.out.println("1. Active");
            System.out.println("2. Expired");
            System.out.println("3. Suspended");
            System.out.print("-> Select Status (1-3): ");
            String statusChoice = scanner.nextLine();

            if (statusChoice.equals("1")) { status = "Active"; break; }
            else if (statusChoice.equals("2")) { status = "Expired"; break; }
            else if (statusChoice.equals("3")) { status = "Suspended"; break; }
            else {
                System.out.println("[ WARNING ] Invalid choice! Please select from 1 to 3.");
            }
        }
        return new Member(username, password, fullName, type, status);
    }

    private Trainer createNewTrainer(String username, String password, String fullName, Scanner scanner) {
        System.out.print("Enter Specialty (e.g., Yoga, Weightlifting): ");
        String specialty = scanner.nextLine();
        return new Trainer(username, password, fullName, specialty);
    }
}