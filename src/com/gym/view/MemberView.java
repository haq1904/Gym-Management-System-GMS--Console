package com.gym.view;

import com.gym.manage.MemberShipManagement;
import com.gym.manage.WorkoutScheduleManagement;
import com.gym.model.users.Member;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import java.util.Scanner;

public class MemberView implements IDisplayMenu {

    private Scanner scanner = new Scanner(System.in);
    private MemberShipManagement memberShipManagement;
    private Member loggedInMember;

    @Override
    public void displayMenu(GymContext context, User loggedInUser) {
        // Khởi tạo chuyên viên xử lý logic lịch tập (Truyền thẳng Context vào như ông đã chốt)
        WorkoutScheduleManagement scheduleManager = new WorkoutScheduleManagement(context);
        memberShipManagement = new MemberShipManagement(context);
        loggedInMember = (Member) loggedInUser;

        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=========================================");
            System.out.println("               MEMBER MENU               ");
            System.out.println("         Welcome: " + loggedInMember.getFullName());
            System.out.println("=========================================");
            System.out.println("1. View my workout schedules");
            System.out.println("2. Update workout progress");
            System.out.println("3. View my profile");
            System.out.println("0. Logout");
            System.out.println("=========================================");
            System.out.print("-> Select an option (0-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Gọi tính năng 1: Xem lịch (Truyền Username của chính Member đang đăng nhập)
                    scheduleManager.displaySchedules(loggedInMember.getUsername(), false);
                    break;

                case "2":
                    // Gọi tính năng 2: Cập nhật tiến độ
                    scheduleManager.updateProgress(loggedInMember.getUsername(), false);
                    break;

                case "3":
                    // Tính năng 3: Xem profile cá nhân
                    displayViewProfileMenu();
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

    private void displayViewProfileMenu() {
        boolean isViewing = true;

        while (isViewing) {
            // ===== HIỂN THỊ THÔNG TIN CHI TIẾT =====
            System.out.println("\n+=============================================+");
            System.out.println("|                MY PROFILE                   |");
            System.out.println("+=============================================+");
            System.out.println("|                                             |");
            System.out.printf("|  Full Name     :  %-23s |\n", loggedInMember.getFullName());
            System.out.printf("|  Username      :  %-23s |\n", loggedInMember.getUsername());
            System.out.printf("|  Password      :  %-23s |\n", loggedInMember.getPassword());
            System.out.printf("|  Membership    :  %-23s |\n", loggedInMember.getMembershipType());
            System.out.printf("|  Status        :  %-23s |\n", loggedInMember.getSubscriptionStatus());
            System.out.printf("|  Progress      :  %-23s |\n", loggedInMember.getWorkoutProgress());
            System.out.println("|                                             |");
            System.out.println("+=============================================+");

            System.out.println("\n--- PROFILE OPTIONS ---");
            System.out.println("1.Edit my profile (Username, Password, Full Name)");
            System.out.println("0.Back to Member Menu");
            System.out.print("-> Select an option (0-1): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Gọi hàm update với isAdmin = false -> chỉ cho sửa username, password, fullName
                    memberShipManagement.handleUpdateProfileMember(loggedInMember, false);
                    break;

                case "0":
                    System.out.println("[ INFO ] Returning to Member Menu...");
                    isViewing = false;
                    break;

                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter 0 or 1.");
                    break;
            }
        }
    }

}
