package com.gym.manage;

import com.gym.model.users.Member;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberShipManagement {

    private Scanner scanner = new Scanner(System.in);
    IRepository<User> userRepo;
    List<User> userList;

    public MemberShipManagement(GymContext context) {
        userRepo = context.getUserRepo();
        userList = context.getUserList();
    }

    public void handleAddMember() {
        System.out.println("\n[ ADD NEW MEMBER ]('0' to cancel)");

        List<String> currentUsernames = new ArrayList<>();
        for (User u : userList) {
            currentUsernames.add(u.getUsername());
        }
        String newUserName = Helper.generateNextId("member", currentUsernames, "%03d");

        // Dùng println để xuống dòng cho đẹp
        System.out.println("Auto generated username : " + newUserName);

        // --- NHẬP PASSWORD ---
        System.out.print("Enter Password: ");
        String newPassword = scanner.nextLine().trim();
        if (newPassword.equals("0")) {
            System.out.println("[ INFO ] Cancelled adding new member.");
            return; // Thoát ngang khỏi hàm, quay lại menu
        }

        // --- NHẬP FULL NAME ---
        System.out.print("Enter Full Name: ");
        String newFullName = scanner.nextLine().trim();
        if (newFullName.equals("0")) {
            System.out.println("[ INFO ] Cancelled adding new member.");
            return; // Thoát ngang khỏi hàm, quay lại menu
        }

        Member newMember = createNewMember(newUserName, newPassword, newFullName);
        if (newMember == null) {
            return;
        }
        userRepo.add(userList, newMember);
        System.out.println("[ SUCCESS ] Member '" + newUserName + "' has been successfully added!");
    }

    public void handleUpdateProfileMember(Member targetMember, boolean isAdmin) {
        // Nếu Admin gọi: tìm member theo username nhập vào
        if (isAdmin) {
            System.out.println("\n[ UPDATE MEMBER ]");
            System.out.print("Enter Username of the member to update: ");
            String usernameToUpdate = scanner.nextLine().trim();

            targetMember = null;
            for (User u : userList) {
                if (u.getUsername().equals(usernameToUpdate) && u instanceof Member) {
                    targetMember = (Member) u;
                    break;
                }
            }

            if (targetMember == null) {
                System.out.println("[ ERROR ] Member with username '" + usernameToUpdate + "' not found!");
                return;
            }
        }

        // Hiển thị thông tin hiện tại
        System.out.println("\n[ INFO ] Current Info of " + targetMember.getFullName() + " (" + targetMember.getUsername() + "):");
        System.out.println("- Password: " + targetMember.getPassword());
        if (isAdmin) {
            System.out.println("- Membership Type: " + targetMember.getMembershipType());
            System.out.println("- Subscription Status: " + targetMember.getSubscriptionStatus());
        }
        System.out.println("----------------------------------------");

        System.out.println("What do you want to update?");
        if (isAdmin) {
            // Admin: full options
            System.out.println("1. Renew / Change Subscription Plan");
            System.out.println("2. Update Membership Status (Active/Expired/Suspended)");
            System.out.println("3. Update Password");
            System.out.println("4. Update Full Name");
            System.out.println("5. Update Username");
            System.out.print("-> Choice (1-5): ");
        } else {
            // Member: chỉ 3 lựa chọn cơ bản
            System.out.println("1. Update Username");
            System.out.println("2. Update Password");
            System.out.println("3. Update Full Name");
            System.out.print("-> Choice (1-3): ");
        }

        String updateChoice = scanner.nextLine().trim();

        if (isAdmin) {
            // ===== ADMIN FLOW =====
            if (updateChoice.equals("1")) {
                System.out.println("1. Trial (1 Month) | 2. Newbie (3 Months) | 3. VIP (6 Months) | 4. Premium (12 Months)");
                System.out.print("-> Select new plan (1-4): ");
                String planChoice = scanner.nextLine().trim();
                String newPlan = "";

                if (planChoice.equals("1")) {
                    newPlan = "Trial (1 Month)";
                } else if (planChoice.equals("2")) {
                    newPlan = "Newbie (3 Months)";
                } else if (planChoice.equals("3")) {
                    newPlan = "VIP (6 Months)";
                } else if (planChoice.equals("4")) {
                    newPlan = "Premium (12 Months)";
                } else {
                    System.out.println("[ WARNING ] Invalid choice! Update canceled.");
                    return;
                }

                targetMember.renewSubscription(newPlan);
                userRepo.saveData(userList);
                System.out.println("[ SUCCESS ] Subscription plan updated to database.");
            } else if (updateChoice.equals("2")) {
                System.out.println("1. Active | 2. Expired | 3. Suspended");
                System.out.print("-> Select new status (1-3): ");
                String statusChoice = scanner.nextLine().trim();
                String newStatus = "";

                if (statusChoice.equals("1")) {
                    newStatus = "Active";
                } else if (statusChoice.equals("2")) {
                    newStatus = "Expired";
                } else if (statusChoice.equals("3")) {
                    newStatus = "Suspended";
                } else {
                    System.out.println("[ WARNING ] Invalid choice! Update canceled.");
                    return;
                }

                targetMember.setSubscriptionStatus(newStatus);
                userRepo.saveData(userList);
                System.out.println("[ SUCCESS ] Status updated to database.");
            } else if (updateChoice.equals("3")) {
                System.out.print("Enter new Password: ");
                String newPass = scanner.nextLine().trim();

                if (!newPass.isEmpty()) {
                    targetMember.setPassword(newPass);
                    userRepo.saveData(userList);
                    System.out.println("[ SUCCESS ] Password updated successfully.");
                } else {
                    System.out.println("[ WARNING ] Password cannot be empty! Update canceled.");
                }
            } else if (updateChoice.equals("4")) {
                System.out.print("Enter new Full Name: ");
                String newName = scanner.nextLine().trim();

                if (!newName.isEmpty()) {
                    targetMember.setFullName(newName);
                    userRepo.saveData(userList);
                    System.out.println("[ SUCCESS ] Full Name updated successfully to: " + newName);
                } else {
                    System.out.println("[ WARNING ] Full Name cannot be empty! Update canceled.");
                }
            } else if (updateChoice.equals("5")) {
                handleUpdateUsername(targetMember);
            } else {
                System.out.println("[ WARNING ] Invalid option. Update canceled.");
            }
        } else {
            // ===== MEMBER FLOW (chỉ 3 lựa chọn) =====
            if (updateChoice.equals("1")) {
                handleUpdateUsername(targetMember);
            } else if (updateChoice.equals("2")) {
                System.out.print("Enter new Password: ");
                String newPass = scanner.nextLine().trim();

                if (!Helper.isValidInput(newPass)) {
                    System.out.println("[ WARNING ] Password must contain only letters (a-z, A-Z) and numbers (0-9).");
                    System.out.println("            No special characters, accents, or spaces allowed! Update canceled.");
                    return;
                }

                targetMember.setPassword(newPass);
                userRepo.saveData(userList);
                System.out.println("[ SUCCESS ] Password updated successfully.");
            } else if (updateChoice.equals("3")) {
                System.out.print("Enter new Full Name: ");
                String newName = scanner.nextLine().trim();

                if (!newName.isEmpty()) {
                    targetMember.setFullName(newName);
                    userRepo.saveData(userList);
                    System.out.println("[ SUCCESS ] Full Name updated successfully to: " + newName);
                } else {
                    System.out.println("[ WARNING ] Full Name cannot be empty! Update canceled.");
                }
            } else {
                System.out.println("[ WARNING ] Invalid option. Update canceled.");
            }
        }
    }

    private void handleUpdateUsername(Member targetMember) {
        System.out.print("Enter new Username: ");
        String newUsername = scanner.nextLine().trim();

        if (!Helper.isValidInput(newUsername)) {
            System.out.println("[ WARNING ] Username must contain only letters (a-z, A-Z) and numbers (0-9).");
            System.out.println("            No special characters, accents, or spaces allowed! Update canceled.");
            return;
        }

        // Kiểm tra trùng username
        for (User u : userList) {
            if (u.getUsername().equalsIgnoreCase(newUsername)) {
                System.out.println("[ WARNING ] Username '" + newUsername + "' already exists! Update canceled.");
                return;
            }
        }

        String oldUsername = targetMember.getUsername();
        targetMember.setUsername(newUsername);
        userRepo.saveData(userList);
        System.out.println("[ SUCCESS ] Username updated: " + oldUsername + " -> " + newUsername);
    }

    public void handleDeleteMember() {
        System.out.println("\n[ DELETE MEMBER ]");
        System.out.print("Enter the Username of member to delete: ");
        String usernameToDelete = scanner.nextLine().trim();

        boolean isTargetAMember = false;

        for (User u : userList) {
            if (u.getUsername().equals(usernameToDelete) && u instanceof Member) {
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

    public Member createNewMember(String username, String password, String fullName) {
        String type = "";
        String status = "";

        // --- CHỌN GÓI TẬP ---
        while (true) {
            System.out.println("\n[ MEMBERSHIP TYPE ]");
            System.out.println("1. Trial (1 Month)");
            System.out.println("2. Newbie (3 Months)");
            System.out.println("3. VIP (6 Months)");
            System.out.println("4. Premium (12 Months)");
            System.out.print("-> Select Membership Type (0-4): ");
            String typeChoice = scanner.nextLine().trim();

            if (typeChoice.equals("0")) {
                System.out.println("[ INFO ] Cancelled adding new member.");
                return null; // Bấm 0 thì trả về null
            } else if (typeChoice.equals("1")) {
                type = "Trial (1 Month)";
                break;
            } else if (typeChoice.equals("2")) {
                type = "Newbie (3 Months)";
                break;
            } else if (typeChoice.equals("3")) {
                type = "VIP (6 Months)";
                break;
            } else if (typeChoice.equals("4")) {
                type = "Premium (12 Months)";
                break;
            } else {
                System.out.println("[ WARNING ] Invalid choice! Please select from 0 to 4.");
            }
        }

        // --- CHỌN TRẠNG THÁI ---
        while (true) {
            System.out.println("\n[ MEMBERSHIP STATUS ]");
            System.out.println("1. Active");
            System.out.println("2. Expired");
            System.out.println("3. Suspended");
            System.out.println("0. Cancel and exit"); // Thêm option 0

            String statusChoice = scanner.nextLine().trim();

            if (statusChoice.equals("0")) {
                System.out.println("[ INFO ] Cancelled adding new member.");
                return null; // Bấm 0 thì trả về null
            } else if (statusChoice.equals("1")) {
                status = "Active";
                break;
            } else if (statusChoice.equals("2")) {
                status = "Expired";
                break;
            } else if (statusChoice.equals("3")) {
                status = "Suspended";
                break;
            } else {
                System.out.println("[ WARNING ] Invalid choice! Please select from 0 to 3.");
            }
        }

        return new Member(username, password, fullName, type, status);
    }

    public void handleViewMemberInfo() {
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

                    if (statusChoice.equals("1")) {
                        searchStatus = "Active";
                    } else if (statusChoice.equals("2")) {
                        searchStatus = "Expired";
                    } else if (statusChoice.equals("3")) {
                        searchStatus = "Suspended";
                    } else {
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
