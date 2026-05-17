package com.gym.model;

public class Member extends User {
    private String membershipType; // Loai goi tap
    private String subscriptionStatus; // Trang thai(Activated , Expired)

    public String getMembershipType() {
        return membershipType;
    }

    //Get status
    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public String getWorkoutProgress() {
        return workoutProgress;
    }

    private String workoutProgress; // Tien do)

    public Member(String username, String password, String fullName, String membershipType, String subscriptionStatus) {
        super(username, password, fullName, "Member");
        this.membershipType = membershipType;
        this.subscriptionStatus = subscriptionStatus;
        this.workoutProgress = "Chưa có thông tin";
    }

    public Member() {
        super();
        this.role = "Member";
        this.subscriptionStatus = "Expired"; // Mặc định chưa nộp tiền là Expired
    }

    //Xem lich tap
    public void viewWorkoutSchedule() {
        // Code gọi ScheduleRepository để in lịch tập của hội viên này ra màn hình
    }

    //Update tien do
    public void updateProgress(String newProgress) {
        this.workoutProgress = newProgress;
        System.out.println("Cập nhật tiến độ thành công!");
    }

    //Gia han goi tap
    public void renewSubscription(String newPlan) {
        this.membershipType = newPlan;
        this.subscriptionStatus = "Active";
        System.out.println("Gia hạn thành công gói: " + newPlan);
    }

    @Override
    public void displayMenu() {
        // Hiển thị menu gọi đến các hàm trên
    }

    // Đừng quên tạo các hàm Getter/Setter cho các thuộc tính mới nhé!
}
