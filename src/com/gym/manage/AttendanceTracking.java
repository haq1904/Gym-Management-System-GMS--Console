package com.gym.manage;

import com.gym.model.schedule.WorkoutSchedule;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AttendanceTracking {
    private Scanner scanner = new Scanner(System.in);
    private List<WorkoutSchedule> scheduleList;
    private List<User> userList;
    private GymContext context;

    public AttendanceTracking(GymContext context) {
        this.context=context;
        this.scheduleList = context.getSchedulesList();
        this.userList = context.getUserList();
    }

    public void handleViewAllHistory(String username, boolean isAdmin) {
        List<WorkoutSchedule> reportList = new ArrayList<>();
        for (WorkoutSchedule s : scheduleList) {
            if (isAdmin )
                reportList.add(s);
            else if(username.equals(s.getTrainerUsername()))
                reportList.add(s);
        }
        printReportTable(reportList, "ALL HISTORY", isAdmin);
    }

    public void handleFilterByDate(String username, boolean isAdmin) {
        List<WorkoutSchedule> reportList = new ArrayList<>();
        while (true) {
            System.out.print("Enter Date to filter (dd/MM/yyyy) or '0' to go back: ");
            String dateInput = scanner.nextLine().trim();

            if (dateInput.equals("0")) return;
            if (!dateInput.matches("^[0-9]{2}/[0-9]{2}/[0-9]{4}$")) {
                System.out.println("[ ERROR ] Invalid Date format!");
                continue;
            }

            for (WorkoutSchedule s : scheduleList) {
                if (s.getDate().equals(dateInput)) {
                    if (!isAdmin && !s.getTrainerUsername().equalsIgnoreCase(username)) continue;
                    reportList.add(s);
                }
            }
            printReportTable(reportList, "DATE: " + dateInput, isAdmin);
            break;
        }
    }

    public void handleFilterByMember(String username, boolean isAdmin) {
        List<WorkoutSchedule> reportList = new ArrayList<>();
        System.out.print("Enter Member's Name or Username to filter (or '0' to go back): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;

        String searchKeyword = removeAccents(input).toLowerCase();

        for (WorkoutSchedule s : scheduleList) {

            if (!isAdmin && !s.getTrainerUsername().equalsIgnoreCase(username)) continue;

            String memberUser = s.getMemberUsername().toLowerCase();
            String memberFullName = removeAccents(getUserFullName(s.getMemberUsername())).toLowerCase();

            if (memberUser.contains(searchKeyword) || memberFullName.contains(searchKeyword)) {
                reportList.add(s);
            }

        }
        printReportTable(reportList, "MEMBER SEARCH: " + input, isAdmin);
    }

    public void handleFilterByTrainer(String username, boolean isAdmin) {

        List<WorkoutSchedule> reportList = new ArrayList<>();
        System.out.print("Enter Trainer's Name or Username to filter (or '0' to go back): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;

        String searchKeyword = removeAccents(input).toLowerCase();

        for (WorkoutSchedule s : scheduleList) {
            String trainerUser = s.getTrainerUsername().toLowerCase();
            String trainerFullName = removeAccents(getUserFullName(s.getTrainerUsername())).toLowerCase();

            if (trainerUser.contains(searchKeyword) || trainerFullName.contains(searchKeyword)) {
                    reportList.add(s);
            }
        }
        printReportTable(reportList, "TRAINER SEARCH: " + input, isAdmin);
    }

    private void printReportTable(List<WorkoutSchedule> list, String filterType, boolean isAdmin) {
        if (list.isEmpty()) {
            System.out.println("[ INFO ] No attendance records found matching this criteria.");
            return;
        }

        DateTimeFormatter dFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter tFmt = DateTimeFormatter.ofPattern("HH:mm");
        list.sort((s1, s2) -> {
            int dateComp = LocalDate.parse(s1.getDate(), dFmt).compareTo(LocalDate.parse(s2.getDate(), dFmt));
            if (dateComp != 0) return dateComp;
            return LocalTime.parse(s1.getTime(), tFmt).compareTo(LocalTime.parse(s2.getTime(), tFmt));
        });

        System.out.println("\n---------------------------------------------------------------------------------------------------------");
        System.out.printf("ATTENDANCE REPORT [%s] - Total: %d records\n", filterType.toUpperCase(), list.size());
        System.out.println("---------------------------------------------------------------------------------------------------------");

        // Phân nhánh in tiêu đề bảng
        if (isAdmin) {
            System.out.printf("%-8s | %-10s | %-6s | %-20s | %-12s | %-20s | %-12s | %-10s\n",
                    "SCH-ID", "Date", "Time", "Member Name", "Member User", "Trainer Name", "Trainer User", "Status");
        } else {
            // View của Trainer: Cắt bỏ cột Trainer đi cho rộng rãi, vẫn giữ ID và Username của Member
            System.out.printf("%-8s | %-10s | %-6s | %-25s | %-15s | %-10s\n",
                    "SCH-ID", "Date", "Time", "Member Name", "Member Username", "Status");
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (WorkoutSchedule s : list) {
            String memberName = getUserFullName(s.getMemberUsername());

            if (isAdmin) {
                String trainerName = getUserFullName(s.getTrainerUsername());

                System.out.printf("%-8s | %-10s | %-6s | %-20s | %-12s | %-20s | %-12s | %-10s\n",
                        s.getScheduleId(), s.getDate(), s.getTime(), memberName, s.getMemberUsername(), trainerName, s.getTrainerUsername(), s.getProgressStatus());
            } else {
                System.out.printf("%-8s | %-10s | %-6s | %-25s | %-15s | %-10s\n",
                        s.getScheduleId(), s.getDate(), s.getTime(), memberName, s.getMemberUsername(), s.getProgressStatus());
            }
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    private String getUserFullName(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) return u.getFullName();
        }
        return username;
    }

    private String removeAccents(String str) {
        if (str == null) return null;
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }

    public void handleViewAttendanceSummary(String username, boolean isAdmin) {
        System.out.println("\n[ FEATURE ] Compiling Gym Data... Please wait...");
        ReportManagement reportManager = new ReportManagement(this.context);
        if (isAdmin) {
            reportManager.generateAdminAttendanceSummary();
        } else {
            reportManager.generateTrainerAttendanceSummary(username);
        }
    }

    public void handleViewRevenueAndMembership() {
        ReportManagement reportManager = new ReportManagement(this.context);

        System.out.println("\n=============================================================================================================");
        System.out.println("                    SUBSCRIPTION REVENUE & MEMBERSHIP OVERVIEW");
        System.out.println("=============================================================================================================");

        // === SECTION 1: REVENUE SUMMARY ===
        double totalRevenue = reportManager.calculateTotalRevenue();
        int[] statusCounts = reportManager.countMembershipsByStatus();
        int totalMembers = statusCounts[0] + statusCounts[1] + statusCounts[2];
        double avgRevenue = (statusCounts[0] > 0) ? totalRevenue / statusCounts[0] : 0;

        System.out.println("\n[ 1. REVENUE SUMMARY ]");
        System.out.printf(" TOTAL REVENUE (Active Subs)    : $%,.2f\n", totalRevenue);
        System.out.printf(" Average Revenue / Active Member : $%,.2f\n", avgRevenue);
        System.out.printf(" Active Paying Members           : %d / %d\n", statusCounts[0], totalMembers);

        // === SECTION 2: REVENUE BREAKDOWN BY PLAN ===
        System.out.println("\n[ 2. REVENUE BREAKDOWN BY PLAN ]");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-28s | %-10s | %-12s | %-12s\n", "Plan Name", "Members", "Unit Price", "Subtotal");
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        Map<String, double[]> revenueByPlan = reportManager.getRevenueByPlan();
        for (Map.Entry<String, double[]> entry : revenueByPlan.entrySet()) {
            double[] data = entry.getValue(); // [0]=count, [1]=totalRevenue, [2]=unitPrice
            System.out.printf(" %-28s | %-10.0f | $%-11.2f | $%-11.2f\n",
                    entry.getKey(), data[0], data[2], data[1]);
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf(" %-28s | %-10s | %-12s | $%-11.2f\n", "TOTAL", "", "", totalRevenue);

        // === SECTION 3: MEMBERSHIP STATUS OVERVIEW ===
        System.out.println("\n[ 3. MEMBERSHIP STATUS OVERVIEW ]");
        System.out.println(" Total Members in System : " + totalMembers);

        String[] statusLabels = {"Active", "Expired", "Suspended"};
        for (int i = 0; i < 3; i++) {
            double pct = (totalMembers > 0) ? (double) statusCounts[i] / totalMembers * 100 : 0;
            System.out.printf(" - %-12s : %3d  (%.1f%%)\n", statusLabels[i], statusCounts[i], pct);
        }

        // === SECTION 4: MEMBERSHIP TYPE DISTRIBUTION ===
        System.out.println("\n[ 4. MEMBERSHIP TYPE DISTRIBUTION ]");
        String mostPopular = reportManager.getMostPopularPlan();
        System.out.println(" Most Popular Plan : " + mostPopular);
        System.out.println();

        Map<String, Integer> distribution = reportManager.getMembershipTypeDistribution();
        for (Map.Entry<String, Integer> entry : distribution.entrySet()) {
            double pct = (totalMembers > 0) ? (double) entry.getValue() / totalMembers * 100 : 0;
            String star = entry.getKey().equals(mostPopular) ? " *" : "";
            System.out.printf(" - %-28s : %2d member(s)  (%.1f%%)%s\n",
                    entry.getKey(), entry.getValue(), pct, star);
        }

        System.out.println("\n=============================================================================================================");
    }
}