package com.gym.model;

import java.util.List;

public class Admin extends User {
    public Admin(String username, String password, String fullName) {
        super(username, password, fullName, "Admin");
    }

    public Admin() {
        super();
        this.role = "Admin"; // hard code cho role
    }

    //Them va xoa member
    public void addMember(List<User> userList, Member newMember) {
        userList.add(newMember);
        // Sau đó gọi DatabaseManager/Repository để save đè lên file users.csv
    }

    public void deleteMember(List<User> userList, String memberUsername) {
        // Code tìm kiếm memberUsername trong userList và remove()
    }

    //Quan ly co so và trainer
    public void manageFacilitiesAndTrainers() {
        // Logic menu con cho phép xem/thêm/xóa PT
    }

    //Xuat bao cao ve doanh thu
    public void generateRevenueReport(List<User> userList) {
        // Code duyệt qua userList, tìm các Member có status "Active",
        // dựa vào gói tập để tính ra tổng tiền và in ra màn hình
    }

    //Xuat bao cao diem danh
    public void generateAttendanceReport() {
        // Code load dữ liệu từ attendance.csv lên và thống kê
    }

    @Override
    public void displayMenu() {

    }
}