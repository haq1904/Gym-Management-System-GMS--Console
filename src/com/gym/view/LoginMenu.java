package com.gym.view;

import com.gym.model.facilities.GymMachine;
import com.gym.model.users.User;
import com.gym.repository.GymContext;

import java.util.List;
import java.util.Scanner;

public class LoginMenu {
    private Scanner scanner;

    public LoginMenu() {
        //Dung de doc du lieu tu nguoi dung
        this.scanner = new Scanner(System.in);
    }

    //
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public User displayLogin(GymContext gymContext) {
        List<User> userList = gymContext.getUserList();
        while (true) {
            clearScreen();
            System.out.println("=========================================");
            System.out.println("       GYM MANAGEMENT SYSTEM (GMS)       ");
            System.out.println("=========================================");
            System.out.println("1. Login to the system");
            System.out.println("0. Exit program");
            System.out.println("=========================================");
            System.out.print("Select an option (0-1): ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("-> Enter Username: ");
                String username = scanner.nextLine();
                System.out.print("-> Enter Password: ");
                String password = scanner.nextLine();

                // Authenticate user
                User authenticatedUser = authenticate(userList, username, password);

                if (authenticatedUser != null) {
                    System.out.println("\n[ SUCCESS ] Login successful!");
                    System.out.println("Welcome " + authenticatedUser.getRole() + ": " + authenticatedUser.getFullName() + ".");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    return authenticatedUser;
                } else {
                    System.out.println("\n[ FAILED ] Invalid username or password! Please try again.");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                }
            } else if (choice.equals("0")) {
                System.out.println("\nThank you for using GMS! Goodbye.");
                System.exit(0);
            } else {
                System.out.println("\n[ WARNING ] Invalid choice! Please try again.");
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    //Ham xac thuc user
    private User authenticate(List<User> userList, String username, String password) {
        for (User user : userList){
            if (user.getUsername().equals(username) && user.checkPassword(password)) { // [cite: 32]
                return user;
            }
        }
        return null;
    }
}

