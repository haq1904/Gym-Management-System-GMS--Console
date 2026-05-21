package com.gym.manage;

import com.gym.model.users.Member;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;

import java.util.List;
import java.util.Scanner;

public class MemberShipManagement {
    private Scanner scanner = new Scanner(System.in);
    IRepository<User> userRepo;
    List<User> userList ;

    public MemberShipManagement(GymContext context) {
        userRepo = context.getUserRepo();
        userList = context.getUserList();
    }


    public void displayMemberManagementMenu() {
        boolean isManaging = true;

        while (isManaging) {
            System.out.println("\n--- MEMBER MANAGEMENT MENU ---");
            System.out.println("1. Add a new Member");
            System.out.println("2. Update Profile");
            System.out.println("3. Delete a Member");
            System.out.println("4. View Member Information");
            System.out.println("0. Back to Main Admin Menu");
            System.out.print("-> Select an option (0-3): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": // --- TÍNH NĂNG THÊM MEMBER ---
                    handleAddMember();
                    break;
                case "2": // --- TÍNH NĂNG UPDATE MEMBER ---
                    handleUpdateProfileMember();
                    break;
                case "3": // --- TÍNH NĂNG DELETE MEMBER ---
                    handleDeleteMember();
                    break;
                case "4": // --- TÍNH NĂNG DELETE MEMBER ---
                    handleViewMemberInfo();
                    break;
                case "0":
                    System.out.println("[ INFO ] Returning to Main Menu...");
                    isManaging = false;
                    break;
                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter from 0 to 3.");
                    break;
            }
        }
    }

    private void handleAddMember(){
        System.out.println("\n[ ADD NEW MEMBER ]");
        String newUsername = "";
        while (true) {
            System.out.print("Enter Username: ");
            newUsername = scanner.nextLine().trim();

            if (!newUsername.matches("^[a-zA-Z0-9]+$")) {
                System.out.println("[ ERROR ] Invalid Username!");
                System.out.println("Please use ONLY unaccented letters and numbers. No spaces or special characters allowed.");
                continue;
            }

            boolean isDuplicate = false;
            for (User u : userList) {
                // Sửa thành equalsIgnoreCase
                if (u.getUsername().equals(newUsername)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) {
                System.out.println("[ ERROR ] Username '" + newUsername + "' already exists! Please try another one.");
            } else {
                break;
            }
        }

        System.out.print("Enter Password: ");
        String newPassword = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String newFullName = scanner.nextLine().trim();

        Member newMember = createNewMember(newUsername, newPassword, newFullName);
        userRepo.add(userList, newMember);
    }

    private void handleUpdateProfileMember(){
        System.out.println("\n[ UPDATE MEMBER ]");
        System.out.print("Enter Username of the member to update: ");
        String usernameToUpdate = scanner.nextLine().trim();

        Member foundMember = null;
        for (User u : userList) {
            if (u.getUsername().equals(usernameToUpdate) && u instanceof Member) {
                foundMember = (Member) u;
                break;
            }
        }

        if (foundMember == null) {
            System.out.println("[ ERROR ] Member with username '" + usernameToUpdate + "' not found!");
        } else {
            System.out.println("\n[ INFO ] Current Info of " + foundMember.getFullName() + " (" + foundMember.getUsername() + "):");
            System.out.println("- Password: " + foundMember.getPassword());
            System.out.println("- Membership Type: " + foundMember.getMembershipType());
            System.out.println("- Subscription Status: " + foundMember.getSubscriptionStatus());
            System.out.println("----------------------------------------");

            System.out.println("What do you want to update?");
            System.out.println("1. Renew / Change Subscription Plan");
            System.out.println("2. Update Membership Status (Active/Expired/Suspended)");
            System.out.println("3. Update Password");
            System.out.println("4. Update Full Name");
            System.out.print("-> Choice (1-4): ");
            String updateChoice = scanner.nextLine().trim();

            if (updateChoice.equals("1")) {
                System.out.println("1. Trial (1 Month) | 2. Newbie (3 Months) | 3. VIP (6 Months) | 4. Premium (12 Months)");
                System.out.print("-> Select new plan (1-4): ");
                String planChoice = scanner.nextLine().trim();
                String newPlan = "";

                if (planChoice.equals("1")) newPlan = "Trial (1 Month)";
                else if (planChoice.equals("2")) newPlan = "Newbie (3 Months)";
                else if (planChoice.equals("3")) newPlan = "VIP (6 Months)";
                else if (planChoice.equals("4")) newPlan = "Premium (12 Months)";
                else {
                    System.out.println("[ WARNING ] Invalid choice! Update canceled.");
                    return; // ĐÃ FIX BUG `break`
                }

                foundMember.renewSubscription(newPlan);
                userRepo.saveData(userList);
                System.out.println("[ SUCCESS ] Subscription plan updated to database.");
            }
            else if (updateChoice.equals("2")) {
                System.out.println("1. Active | 2. Expired | 3. Suspended");
                System.out.print("-> Select new status (1-3): ");
                String statusChoice = scanner.nextLine().trim();
                String newStatus = "";

                if (statusChoice.equals("1")) newStatus = "Active";
                else if (statusChoice.equals("2")) newStatus = "Expired";
                else if (statusChoice.equals("3")) newStatus = "Suspended";
                else {
                    System.out.println("[ WARNING ] Invalid choice! Update canceled.");
                    return; // ĐÃ FIX BUG `break`
                }

                foundMember.setSubscriptionStatus(newStatus);
                userRepo.saveData(userList);
                System.out.println("[ SUCCESS ] Status updated to database.");
            }
            else if (updateChoice.equals("3")) {
                System.out.print("Enter new Password: ");
                String newPass = scanner.nextLine().trim();

                if (!newPass.isEmpty()) {
                    foundMember.setPassword(newPass);
                    userRepo.saveData(userList);
                    System.out.println("[ SUCCESS ] Password updated successfully.");
                } else {
                    System.out.println("[ WARNING ] Password cannot be empty! Update canceled.");
                }
            }
            else if (updateChoice.equals("4")) {
                System.out.print("Enter new Full Name: ");
                String newName = scanner.nextLine().trim();

                if (!newName.isEmpty()) {
                    foundMember.setFullName(newName);
                    userRepo.saveData(userList);
                    System.out.println("[ SUCCESS ] Full Name updated successfully to: " + newName);
                } else {
                    System.out.println("[ WARNING ] Full Name cannot be empty! Update canceled.");
                }
            }
            else {
                System.out.println("[ WARNING ] Invalid option. Update canceled.");
            }
        }
    }

    private void handleDeleteMember(){
        System.out.println("\n[ DELETE MEMBER ]");
        System.out.print("Enter the Username of member to delete: ");
        String usernameToDelete = scanner.nextLine().trim();

        boolean isTargetAMember = false;

        for (User u : userList) {
            if (u.getUsername().equals(usernameToDelete) && u instanceof  Member) {
                isTargetAMember = true;
                break;
            }
        }

        if (isTargetAMember) {
            userRepo.delete(userList, usernameToDelete);
        } else {
            System.out.println("[ ERROR ] Username not found!");
        }
    }

    private Member createNewMember(String username, String password, String fullName) {
        String type = "";
        String status = "";

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
            else { System.out.println("[ WARNING ] Invalid choice! Please select from 1 to 4."); }
        }

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
            else { System.out.println("[ WARNING ] Invalid choice! Please select from 1 to 3."); }
        }
        return new Member(username, password, fullName, type, status);
    }

    private void handleViewMemberInfo() {
        boolean isViewing = true; // Thêm cờ để lặp lại menu này

        while (isViewing) {
            System.out.println("\n[ VIEW MEMBER INFORMATION ]");
            System.out.println("1. View by Username");
            System.out.println("2. View by Subscription Status");
            System.out.println("3. View All Members");
            System.out.println("0. Back");
            System.out.print("-> Select an option (0-3): ");

            String viewChoice = scanner.nextLine().trim();

            switch (viewChoice) {
                case "1": // Lựa chọn 1: Xem theo Username
                    System.out.print("Enter Username to search: ");
                    String searchUsername = scanner.nextLine().trim();
                    Member foundMember = null;

                    for (User u : userList) {
                        if (u.getUsername().equalsIgnoreCase(searchUsername) && u instanceof Member) {
                            foundMember = (Member) u;
                            break;
                        }
                    }

                    if (foundMember != null) {
                        System.out.println("\n[ RESULT ] Found 1 member:");
                        printMemberDetails(foundMember);
                    } else {
                        System.out.println("\n[ RESULT ] Found 0 members with username: " + searchUsername);
                    }
                    break;

                case "2": // Lựa chọn 2: Xem theo Status
                    System.out.println("1. Active | 2. Expired | 3. Suspended");
                    System.out.print("-> Select status to view (1-3): ");
                    String statusChoice = scanner.nextLine().trim();
                    String searchStatus = "";

                    if (statusChoice.equals("1")) searchStatus = "Active";
                    else if (statusChoice.equals("2")) searchStatus = "Expired";
                    else if (statusChoice.equals("3")) searchStatus = "Suspended";
                    else {
                        System.out.println("[ WARNING ] Invalid choice! Action canceled.");
                        break;
                    }

                    int statusCount = 0;
                    // Vòng lặp 1: Đếm số lượng
                    for (User u : userList) {
                        if (u instanceof Member && ((Member) u).getSubscriptionStatus().equalsIgnoreCase(searchStatus)) {
                            statusCount++;
                        }
                    }

                    System.out.println("\n[ RESULT ] Found " + statusCount + " member(s) with status: " + searchStatus);
                    // Vòng lặp 2: In thông tin nếu số lượng > 0
                    if (statusCount > 0) {
                        for (User u : userList) {
                            if (u instanceof Member && ((Member) u).getSubscriptionStatus().equalsIgnoreCase(searchStatus)) {
                                printMemberDetails((Member) u);
                            }
                        }
                    }
                    break;

                case "3": // Lựa chọn 3: Xem tất cả
                    int totalMembers = 0;
                    // Vòng lặp 1: Đếm tổng số Member
                    for (User u : userList) {
                        if (u instanceof Member) {
                            totalMembers++;
                        }
                    }

                    System.out.println("\n[ RESULT ] Total Members in system: " + totalMembers);
                    // Vòng lặp 2: In tất cả ra
                    if (totalMembers > 0) {
                        for (User u : userList) {
                            if (u instanceof Member) {
                                printMemberDetails((Member) u);
                            }
                        }
                    }
                    break;

                case "0":
                    System.out.println("[ INFO ] Returning to Member Menu...");
                    isViewing = false; // Chuyển cờ thành false để thoát vòng lặp, về hàm ngoài
                    break;

                default:
                    System.out.println("[ WARNING ] Invalid option. Please enter from 0 to 3.");
                    break;
            }
        }
    }

    private void printMemberDetails(Member m) {
        System.out.println("----------------------------------------");
        System.out.println("Username      : " + m.getUsername());
        System.out.println("Full Name     : " + m.getFullName());
        System.out.println("Membership    : " + m.getMembershipType());
        System.out.println("Status        : " + m.getSubscriptionStatus());
        System.out.println("Progress      : " + m.getWorkoutProgress());
    }
}
