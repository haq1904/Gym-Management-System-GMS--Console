package com.gym.view;


import com.gym.model.users.User;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;

import java.util.List;
import java.util.Scanner;
import com.gym.manage.*;

public class AdminView implements IDisplayMenu {

    private Scanner scanner = new Scanner(System.in);


    // Hàm khởi chạy Menu chính của Admin
    @Override
    public void displayMenu(GymContext context, User loggedInAdmin) {
        MemberShipManagement memberShipManagement = new MemberShipManagement(context);
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

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n[ FEATURE ] Opening Facilities & Trainers Management...");
                    // Gọi hàm displayFacilitiesManagementMenu() ở đây
                    break;
                case "2":
                    System.out.println("\nOpening Member Management...");
                    memberShipManagement.displayMemberManagementMenu();
                    break;
                case "3":
                    System.out.println("\n[ FEATURE ] Generating statistical reports...");
                    break;
                case "0":
                    System.out.println("\n[ LOGOUT ] Returning to the main screen...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("\n[ WARNING ] Invalid choice. Please enter a number from 0 to 3!");
                    break;
            }

            if (isRunning) {
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }





}