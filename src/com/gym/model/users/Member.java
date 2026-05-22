package com.gym.model.users;

import com.gym.repository.GymContext;
import com.gym.repository.UserRepository;
import com.gym.view.IDisplayMenu;
import com.gym.view.MemberView;

import java.util.List;

public class Member extends User {
    private String membershipType; // Loai goi tap
    private String subscriptionStatus; // Trang thai(Activated , Expired)
    private String workoutProgress; // Tien do

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

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }



    public Member(String username, String password, String fullName, String membershipType, String subscriptionStatus) {
        super(username, password, fullName, "Member");
        this.membershipType = membershipType;
        this.subscriptionStatus = subscriptionStatus;
        this.workoutProgress = "No infor";
    }

    public Member() {
        super();
        this.role = "Member";
        this.subscriptionStatus = "Expired"; // Mặc định chưa nộp tiền là Expired
    }

    @Override
    public IDisplayMenu getMenu() {
        return new MemberView();
    }

    //Update tien do
    public void updateProgress(String newProgress) {
        this.workoutProgress = newProgress;
    }

    //Gia han goi tap
    public void renewSubscription(String newPlan) {
        this.membershipType = newPlan;
        this.subscriptionStatus = "Active";
    }


}
