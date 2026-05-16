package com.gym.view;

import com.gym.model.User;

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

    public User displayLogin(List<User> userList) {
        while (true) {
            clearScreen();
            System.out.println("=========================================");
            System.out.println("   HỆ THỐNG QUẢN LÝ PHÒNG GYM (GMS)      ");
            System.out.println("=========================================");
            System.out.println("1. Đăng nhập vào hệ thống");
            System.out.println("0. Thoát chương trình");
            System.out.println("=========================================");
            System.out.print("Chọn chức năng (0-1): ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("-> Nhập tài khoản (Username): ");
                String username = scanner.nextLine();
                System.out.print("-> Nhập mật khẩu (Password): ");
                String password = scanner.nextLine();

                //Xac thuc tai khoan
                User authenticatedUser = authenticate(userList, username, password);

                if (authenticatedUser != null) {
                    System.out.println("\n[ THÀNH CÔNG ] Đăng nhập thành công!");
                    System.out.println("Xin chào " + authenticatedUser.getRole() + ": " + authenticatedUser.getFullName() + "."); // [cite: 32, 41]
                    System.out.println("Nhấn Enter để tiếp tục vào ứng dụng...");
                    scanner.nextLine();
                    return authenticatedUser;
                } else {
                    System.out.println("\n[ THẤT BẠI ] Sai tài khoản hoặc mật khẩu! Vui lòng thử lại.");
                    System.out.println("Nhấn Enter để tiếp tục...");
                    scanner.nextLine();
                }
            } else if (choice.equals("0")) {
                System.out.println("\nCảm ơn ông đã sử dụng hệ thống GMS! Tạm biệt.");
                System.exit(0);
            } else {
                System.out.println("\n[ CẢNH BÁO ] Lựa chọn không hợp lệ! Vui lòng nhập lại.");
                System.out.println("Nhấn Enter để tiếp tục...");
                scanner.nextLine();
            }
        }
    }

    //Ham xac thuc user
    private User authenticate(List<User> userList, String username, String password) {
        for (User user : userList){
            if (user.getUsername().equals(username) && user.checkPassword(password)) { // [cite: 32]
                return user; // Tìm thấy và đúng pass -> trả về user đó
            }
        }
        return null; // Không tìm thấy tài khoản hợp lệ
    }
}
