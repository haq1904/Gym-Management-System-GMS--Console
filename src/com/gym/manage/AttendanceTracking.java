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
            if (!isAdmin && !s.getTrainerUsername().equals(username)) continue;
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
        // Chỉ dành cho Admin. Trainer không cần lọc theo HLV khác.
        if (!isAdmin) {
            System.out.println("[ INFO ] As a Trainer, you can only view your own reports.");
            return;
        }

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
                if (trainerName.length() > 20) trainerName = trainerName.substring(0, 17) + "...";

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
        if (!isAdmin) {
            System.out.println("[ INFO ] Detailed global summaries are available for Admins only.");
            return;
        }

        ReportManagement reportManager = new ReportManagement(context);
        System.out.println("\n[ FEATURE ] Compiling Gym Data... Please wait...");
        reportManager.generateAdminAttendanceSummary();

    }
}