package com.gym.repository;

import com.gym.model.users.Admin;
import com.gym.model.users.Member;
import com.gym.model.users.Trainer;
import com.gym.model.users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IRepository<User> {

    private String filePath;

    public UserRepository(String filePath) {
        this.filePath = filePath;
    }
    //Ham doc file , chuyen thanh list user

    @Override
    public List<User> loadData() {
        List<User> userList = new ArrayList<>();

        //Su dung buffer de doc tung dong trong file
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                //Bo qua hang trong
                if (line.trim().isEmpty()) continue;

                //tach thanh cach chuoi con theo dau ","
                String[] data = line.split(",");

                if (data.length >=4 && data.length<=6) {
                    String username = data[0].trim();
                    String password = data[1].trim();
                    String fullName = data[2].trim();
                    String role = data[3].trim();
                    switch (role) {
                        case "Admin":
                            userList.add(new Admin(username, password, fullName));
                            break;
                        case "Trainer":
                            String speciality = data[4].trim();
                            userList.add(new Trainer(username, password, fullName, speciality));
                            break;
                        case "Member":
                            String membershipType = data[4].trim();
                            String subscriptionStatus = data[5].trim();
                            userList.add(new Member(username, password, fullName, membershipType, subscriptionStatus));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ Error ] Can not read account's data: " + e.getMessage());
        }

        return userList;
    }

    @Override
    public void saveData(List<User> userList) {
        // Dùng BufferedWriter để ghi file, tham số thứ 2 của FileWriter không để true
        // để nó ghi đè (overwrite) toàn bộ file thay vì viết tiếp vào cuối file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath))) {
            for (User user : userList) {
                StringBuilder sb = new StringBuilder();
                // Nối các thuộc tính chung của User
                sb.append(user.getUsername()).append(",")
                        .append(user.getPassword()).append(",")
                        .append(user.getFullName()).append(",")
                        .append(user.getRole());

                // Ép kiểu (Casting) để lấy các thuộc tính riêng của từng Role
                if (user instanceof Trainer) {
                    Trainer trainer = (Trainer) user;
                    sb.append(",").append(trainer.getSpecialty()); // Đảm bảo class Trainer có hàm getSpecialty()
                }
                else if (user instanceof Member) {
                    Member member = (Member) user;
                    sb.append(",").append(member.getMembershipType()).append(",") // Có hàm getMembershipType()
                            .append(member.getSubscriptionStatus()); // Có hàm getSubscriptionStatus()
                }

                bw.write(sb.toString());
                bw.newLine(); // Xuống dòng cho user tiếp theo
            }
        } catch (IOException e) {
            System.out.println("[ Error ] Can not save account's data: " + e.getMessage());
        }
    }

    @Override
    public boolean add(List<User> userList, User newUser) {
        // 1. Kiểm tra xem username đã tồn tại chưa để tránh trùng lặp
        for (User u : userList) {
            if (u.getUsername().equals(newUser.getUsername())) {
                System.out.println("[ Error ]  '" + newUser.getUsername() + "' existed!");
                return false;
            }
        }

        // 2. Nếu chưa tồn tại, thêm vào danh sách trên RAM
        userList.add(newUser);

        // 3. Đồng bộ ngay lập tức xuống file CSV
        saveData(userList);
        System.out.println("[ Complete ] Added account with full name: " + newUser.getFullName());
        return true;
    }

    /**
     * HÀM 3: Xóa User theo Username
     */
    @Override
    public boolean delete(List<User> userList, String username) {
        User userToDelete = null;

        // 1. Tìm kiếm user trong List
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                userToDelete = u;
                break;
            }
        }

        // 2. Xóa và lưu lại file
        if (userToDelete != null) {
            userList.remove(userToDelete);
            saveData(userList);
            System.out.println("[ Complete ] Deleted username: " + username);
            return true;
        } else {
            System.out.println("[ Error ] Account with username : " + username +" doesn't exist.");
            return false;
        }
    }
}