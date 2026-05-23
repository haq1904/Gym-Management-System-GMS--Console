package com.gym.manage;

import com.gym.model.schedule.WorkoutSchedule;
import com.gym.model.users.Member;
import com.gym.model.users.Trainer;
import com.gym.model.users.User;
import com.gym.repository.GymContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportManagement {
    private List<User> userList;
    private List<WorkoutSchedule> scheduleList;

    public ReportManagement(GymContext context) {
        this.userList = context.getUserList();
        this.scheduleList = context.getSchedulesList();
    }

    class TrainerStat {
        int totalSessions = 0;
        int completedSessions = 0;
        Map<String, Integer> timeFreq = new HashMap<>(); // Đếm khung giờ
    }

    class MemberStat {
        int totalSessions = 0;
        int completedSessions = 0;
        Map<String, Integer> timeFreq = new HashMap<>(); // Đếm khung giờ
        Map<String, Integer> trainerFreq = new HashMap<>(); // Đếm HLV yêu thích
    }


    public void generateAdminAttendanceSummary() {
        int totalFinalized = 0;
        int totalAttended = 0;
        int totalMissed = 0;

        Map<String, Integer> futureDaysCount = new HashMap<>();
        Map<String, TrainerStat> trainerStats = new HashMap<>();
        Map<String, MemberStat> memberStats = new HashMap<>();

        // 1. Khởi tạo Map cho TẤT CẢ Trainer và Member (Để ai chưa có lịch vẫn hiện 0)
        for (User u : userList) {
            if (u instanceof Trainer) trainerStats.put(u.getUsername(), new TrainerStat());
            if (u instanceof Member) memberStats.put(u.getUsername(), new MemberStat());
        }

        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        // 2. Quét danh sách lịch tập đúng 1 lần duy nhất để gom toàn bộ dữ liệu
        for (WorkoutSchedule s : scheduleList) {
            String status = s.getProgressStatus();

            // Tính Overall Attendance
            if (status.equals("Completed") || status.equals("In Progress")) {
                totalAttended++;
                totalFinalized++;
            } else if (status.equals("Absent")) {
                totalMissed++;
                totalFinalized++;
            }

            // Đếm số ca của các ngày trong tương lai (Tính từ hôm nay trở đi)
            try {
                LocalDate schedDate = LocalDate.parse(s.getDate(), dFmt);
                if (!schedDate.isBefore(today)) {
                    futureDaysCount.put(s.getDate(), futureDaysCount.getOrDefault(s.getDate(), 0) + 1);
                }
            } catch (Exception ignored) {}

            // Gom số liệu cho Trainer Workload
            if (trainerStats.containsKey(s.getTrainerUsername())) {
                TrainerStat ts = trainerStats.get(s.getTrainerUsername());
                ts.totalSessions++;
                if (status.equals("Completed")) ts.completedSessions++;
                ts.timeFreq.put(s.getTime(), ts.timeFreq.getOrDefault(s.getTime(), 0) + 1);
            }

            // Gom số liệu cho Member Leaderboard
            if (memberStats.containsKey(s.getMemberUsername())) {
                MemberStat ms = memberStats.get(s.getMemberUsername());
                ms.totalSessions++;
                if (status.equalsIgnoreCase("Completed")) ms.completedSessions++;
                ms.timeFreq.put(s.getTime(), ms.timeFreq.getOrDefault(s.getTime(), 0) + 1);
                ms.trainerFreq.put(s.getTrainerUsername(), ms.trainerFreq.getOrDefault(s.getTrainerUsername(), 0) + 1);
            }
        }

        // =========================================================================
        // IN KẾT QUẢ RA GIAO DIỆN CONSOLE SIÊU ĐẸP
        // =========================================================================
        System.out.println("\n=============================================================================================================");
        System.out.println("                                ATTENDANCE SUMMARIES");
        System.out.println("=============================================================================================================");

        // --- PHẦN 1: OVERALL ATTENDANCE ---
        System.out.println("\n[ 1. OVERALL ATTENDANCE ]");
        System.out.println(" - Total Finalized Sessions : " + totalFinalized);
        System.out.println(" - Total Attended (Completed): " + totalAttended);
        System.out.println(" - Total Missed (Absent)    : " + totalMissed);
        if (totalFinalized > 0) {
            System.out.printf(" OVERALL ATTENDANCE RATE : %.1f%%\n", ((double) totalAttended / totalFinalized) * 100);
        } else {
            System.out.println(" OVERALL ATTENDANCE RATE : N/A");
        }

        // --- PHẦN 2: FUTURE BUSY DAYS ---
        System.out.println("\n[ 2. UPCOMING BUSY DAYS PREDICTION ]");
        List<Map.Entry<String, Integer>> sortedDays = new ArrayList<>(futureDaysCount.entrySet());
        sortedDays.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // Sắp xếp ngày đông nhất lên đầu

        if (sortedDays.isEmpty()) {
            System.out.println(" - No upcoming schedules found.");
        } else {
            for (int i = 0; i < Math.min(5, sortedDays.size()); i++) { // Chỉ show top 5 ngày
                Map.Entry<String, Integer> entry = sortedDays.get(i);
                String overloadTag = entry.getValue() >= 30 ? " [OVERLOAD]" : ""; // Threshold > 30 là đông
                System.out.printf(" - Date: %-12s | Scheduled Classes: %-3d %s\n", entry.getKey(), entry.getValue(), overloadTag);
            }
        }

        // --- PHẦN 3: TRAINER WORKLOAD ---
        System.out.println("\n[ 3. TRAINERS WORKLOAD LEADERBOARD ]");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-12s | %-30s | %-10s | %-10s | %-15s\n", "Username", "Full Name", "Total", "Comp. Rate", "Peak Hour");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        List<Map.Entry<String, TrainerStat>> sortedTrainers = new ArrayList<>(trainerStats.entrySet());

        // BƯỚC 2: Viết luật sắp xếp
        sortedTrainers.sort((e1, e2) -> {
            TrainerStat t1 = e1.getValue();
            TrainerStat t2 = e2.getValue();

            // Tính tỉ lệ % (Tránh lỗi chia cho 0 nếu chưa dạy ca nào)
            double rate1 = (t1.totalSessions == 0) ? 0.0 : (double) t1.completedSessions / t1.totalSessions;
            double rate2 = (t2.totalSessions == 0) ? 0.0 : (double) t2.completedSessions / t2.totalSessions;

            // So sánh giảm dần (từ cao tới thấp)
            int rateComparison = Double.compare(rate2, rate1);

            // Nếu tỉ lệ % bằng nhau, ưu tiên người có Tổng số ca (Total Sessions) nhiều hơn
            if (rateComparison == 0) {
                return Integer.compare(t2.totalSessions, t1.totalSessions);
            }
            return rateComparison;
        });
        for (Map.Entry<String, TrainerStat> entry : sortedTrainers) {
            String uName = entry.getKey();
            TrainerStat ts = entry.getValue();
            String fName = getUserFullName(uName);
            String compRate = ts.totalSessions == 0 ? "N/A" : String.format("%.1f%%", ((double) ts.completedSessions / ts.totalSessions) * 100);
            String peakHour = getTopKey(ts.timeFreq);

            System.out.printf(" %-12s | %-30s | %-10d | %-10s | %-15s\n", uName, fName, ts.totalSessions, compRate, peakHour);
        }

        // --- PHẦN 4: MEMBER LEADERBOARD ---
        System.out.println("\n[ 4. MEMBERS PERFORMANCE LEADERBOARD ]");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-10s | %-35s | %-6s | %-10s | %-9s | %-35s | %-20s | %-8s\n",
                "Username", "Full Name", "Total", "Comp. Rate", "Peak Hour", "Fav. Trainer", "Membership", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        // BƯỚC 1: Chuyển Map thành List
        List<Map.Entry<String, MemberStat>> sortedMembers = new ArrayList<>(memberStats.entrySet());

        // BƯỚC 2: Thuật toán sắp xếp theo tỉ lệ %
        sortedMembers.sort((e1, e2) -> {
            MemberStat m1 = e1.getValue();
            MemberStat m2 = e2.getValue();

            double rate1 = (m1.totalSessions == 0) ? 0.0 : (double) m1.completedSessions / m1.totalSessions;
            double rate2 = (m2.totalSessions == 0) ? 0.0 : (double) m2.completedSessions / m2.totalSessions;

            int rateComparison = Double.compare(rate2, rate1);
            if (rateComparison == 0) {
                return Integer.compare(m2.totalSessions, m1.totalSessions); // Cùng %, ai siêng book lịch hơn xếp trên
            }
            return rateComparison;
        });
        for (Map.Entry<String, MemberStat> entry : sortedMembers) {
            String uName = entry.getKey();
            MemberStat ms = entry.getValue();

            // Tìm đối tượng Member gốc để lấy thuộc tính riêng
            Member m = getMemberObject(uName);
            if (m == null) continue;

            String fName = m.getFullName();

            String compRate = ms.totalSessions == 0 ? "N/A" : String.format("%.1f%%", ((double) ms.completedSessions / ms.totalSessions) * 100);
            String peakHour = getTopKey(ms.timeFreq);
            String favTrainerUser = getTopKey(ms.trainerFreq);
            String favTrainerName = favTrainerUser.equals("N/A") ? "N/A" : getUserFullName(favTrainerUser);

            String mShip = m.getMembershipType() != null ? m.getMembershipType() : "N/A";
            if (mShip.length() > 18) mShip = mShip.substring(0, 16) + "..";

            System.out.printf(" %-10s | %-35s | %-6d | %-10s | %-9s | %-35s | %-20s | %-8s\n",
                    uName, fName, ms.totalSessions, compRate, peakHour, favTrainerName, mShip, m.getSubscriptionStatus());
        }
        System.out.println("===================================================================================================================================================================");
    }

    public void generateTrainerAttendanceSummary(String trainerUsername) {
        int totalAssigned = 0;   // Tổng tất cả các ca được phân công
        int totalFinalized = 0;  // Tổng số ca đã chốt (Completed + Absent)
        int totalAttended = 0;
        int totalMissed = 0;

        Map<String, Integer> pastDaysCount = new HashMap<>();
        Map<String, MemberStat> myMemberStats = new HashMap<>();

        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        // 1. Quét lịch tập, nhặt riêng dữ liệu của HLV này
        for (WorkoutSchedule s : scheduleList) {
            // LỌC: Bỏ qua tất cả các ca của HLV khác
            if (!s.getTrainerUsername().equalsIgnoreCase(trainerUsername)) continue;

            totalAssigned++;
            String status = s.getProgressStatus();

            // Tính Overall Attendance
            if (status.equalsIgnoreCase("Completed")) {
                totalAttended++;
                totalFinalized++;
            } else if (status.equalsIgnoreCase("Absent")) {
                totalMissed++;
                totalFinalized++;
            }

            // Đếm ngày quá khứ CÓ SỐ CA HOÀN THÀNH
            if (status.equalsIgnoreCase("Completed")) {
                try {
                    LocalDate schedDate = LocalDate.parse(s.getDate(), dFmt);
                    if (schedDate.isBefore(today)) {
                        pastDaysCount.put(s.getDate(), pastDaysCount.getOrDefault(s.getDate(), 0) + 1);
                    }
                } catch (Exception ignored) {}
            }

            // Gom số liệu cho Member Leaderboard (Chỉ đếm học viên của HLV này)
            String mUsername = s.getMemberUsername();
            if (!myMemberStats.containsKey(mUsername)) {
                myMemberStats.put(mUsername, new MemberStat()); // Khởi tạo rổ mới nếu chưa có
            }

            MemberStat ms = myMemberStats.get(mUsername);
            ms.totalSessions++;
            if (status.equalsIgnoreCase("Completed")) ms.completedSessions++;
        }

        // =========================================================================
        // IN KẾT QUẢ RA GIAO DIỆN DÀNH RIÊNG CHO TRAINER
        // =========================================================================
        System.out.println("\n===========================================================================================================");
        System.out.println("                              📊 MY CLASSES & PERFORMANCE DASHBOARD 📊");
        System.out.println("                                Trainer: " + getUserFullName(trainerUsername) + " (" + trainerUsername + ")");
        System.out.println("===========================================================================================================");

        // --- PHẦN 1: OVERALL ATTENDANCE ---
        System.out.println("\n[ 1. OVERALL ATTENDANCE ]");
        System.out.println(" - Total Assigned Classes   : " + totalAssigned + " sessions");
        System.out.println(" - Total Attended (Completed): " + totalAttended + " sessions");
        System.out.println(" - Total Missed (Absent)    : " + totalMissed + " sessions");
        if (totalFinalized > 0) {
            System.out.printf(" 🎯 MY STUDENT ATTEN. RATE  : %.1f%%\n", ((double) totalAttended / totalFinalized) * 100);
        } else {
            System.out.println(" 🎯 MY STUDENT ATTEN. RATE  : N/A");
        }

        // --- PHẦN 2: TOP 5 BUSIEST PAST DAYS ---
        System.out.println("\n[ 2. TOP 5 BUSIEST PAST DAYS (By Completed Sessions) ]");
        List<Map.Entry<String, Integer>> sortedDays = new ArrayList<>(pastDaysCount.entrySet());
        sortedDays.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // Xếp giảm dần

        if (sortedDays.isEmpty()) {
            System.out.println(" - No past completed sessions found.");
        } else {
            for (int i = 0; i < Math.min(5, sortedDays.size()); i++) {
                Map.Entry<String, Integer> entry = sortedDays.get(i);
                System.out.printf(" - Date: %-12s | Completed Classes: %-3d\n", entry.getKey(), entry.getValue());
            }
        }

        // --- PHẦN 3: MY STUDENTS LEADERBOARD ---
        System.out.println("\n[ 3. MY STUDENTS LEADERBOARD (Sorted by Completion Rate) ]");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-10s | %-25s | %-6s | %-10s | %-20s | %-8s\n",
                "Username", "Full Name", "Total", "Comp. Rate", "Membership", "Status");
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        List<Map.Entry<String, MemberStat>> sortedMembers = new ArrayList<>(myMemberStats.entrySet());
        sortedMembers.sort((e1, e2) -> {
            MemberStat m1 = e1.getValue();
            MemberStat m2 = e2.getValue();
            double rate1 = (m1.totalSessions == 0) ? 0.0 : (double) m1.completedSessions / m1.totalSessions;
            double rate2 = (m2.totalSessions == 0) ? 0.0 : (double) m2.completedSessions / m2.totalSessions;

            int rateComparison = Double.compare(rate2, rate1);
            if (rateComparison == 0) {
                return Integer.compare(m2.totalSessions, m1.totalSessions); // Cùng %, ai siêng book hơn xếp trên
            }
            return rateComparison;
        });

        for (Map.Entry<String, MemberStat> entry : sortedMembers) {
            String uName = entry.getKey();
            MemberStat ms = entry.getValue();

            Member m = getMemberObject(uName);
            if (m == null) continue;

            String fName = m.getFullName();
            if (fName.length() > 22) fName = fName.substring(0, 20) + "..";

            String compRate = ms.totalSessions == 0 ? "N/A" : String.format("%.1f%%", ((double) ms.completedSessions / ms.totalSessions) * 100);

            String mShip = m.getMembershipType() != null ? m.getMembershipType() : "N/A";
            if (mShip.length() > 18) mShip = mShip.substring(0, 16) + "..";

            System.out.printf(" %-10s | %-25s | %-6d | %-10s | %-20s | %-8s\n",
                    uName, fName, ms.totalSessions, compRate, mShip, m.getSubscriptionStatus());
        }
        System.out.println("===========================================================================================================");
    }

    private String getTopKey(Map<String, Integer> map) {
        if (map.isEmpty()) return "N/A";
        return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private String getUserFullName(String username) {
        for (User u : userList) {
            if (u.getUsername().equalsIgnoreCase(username)) return u.getFullName();
        }
        return username;
    }

    private Member getMemberObject(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username) && u instanceof Member) {
                return (Member) u;
            }
        }
        return null;
    }
}