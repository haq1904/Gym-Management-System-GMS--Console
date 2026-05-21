package com.gym.view;

import com.gym.manage.WorkoutScheduleManagement;
import com.gym.model.users.User;
import com.gym.repository.GymContext;

import java.util.Scanner;

public class MemberView implements IDisplayMenu {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public void displayMenu(GymContext context, User loggedInMember) {
        // Khởi tạo chuyên viên xử lý logic lịch tập (Truyền thẳng Context vào như ông đã chốt)
        WorkoutScheduleManagement scheduleManager = new WorkoutScheduleManagement(context);
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=========================================");
            System.out.println("               MEMBER MENU               ");
            System.out.println("         Welcome: " + loggedInMember.getFullName());
            System.out.println("=========================================");
            System.out.println("1. View my workout schedules");
            System.out.println("2. Update workout progress");
            System.out.println("3. Track subscription status");
            System.out.println("4. Renew subscription");
            System.out.println("0. Logout");
            System.out.println("=========================================");
            System.out.print("-> Select an option (0-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Gọi tính năng 1: Xem lịch (Truyền Username của chính Member đang đăng nhập)
                    scheduleManager.displaySchedules(loggedInMember.getUsername(),false);
                    break;

                case "2":
                    // Gọi tính năng 2: Cập nhật tiến độ
                    scheduleManager.updateProgress(loggedInMember.getUsername(),false);
                    break;

                case "3":
                    // Tính năng 3: Xem trạng thái gói tập (Chưa code logic)
                    System.out.println("\n[ FEATURE ] Track subscription status is under construction...");
                    System.out.println("-> Logic will be updated when Membership/Subscription Management is fully implemented.");
                    break;

                case "4":
                    // Tính năng 4: Gia hạn gói tập (Chưa code logic)
                    System.out.println("\n[ FEATURE ] Renew subscription is under construction...");
                    System.out.println("-> Logic will be updated when Subscription Payment is implemented.");
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