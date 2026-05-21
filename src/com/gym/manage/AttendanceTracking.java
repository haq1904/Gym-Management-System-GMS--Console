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

    public AttendanceTracking(GymContext context) {
        this.scheduleList = context.getSchedulesList();
        this.userList = context.getUserList();
    }

    public void handleViewAllHistory() {
        printAdminReportTable(scheduleList, "ALL HISTORY");
    }

    public void handleFilterByDate() {
        List<WorkoutSchedule> reportList = new ArrayList<>();
        while (true) {
            System.out.print("Enter Date to filter (dd/MM/yyyy) or '0' to go back: ");
            String dateInput = scanner.nextLine().trim();

            if (dateInput.equals("0")) return;

            if (!dateInput.matches("^[0-9]{2}/[0-9]{2}/[0-9]{4}$")) {
                System.out.println("[ ERROR ] Invalid Date format! Please use dd/MM/yyyy.");
                continue;
            }

            for (WorkoutSchedule s : scheduleList) {
                if (s.getDate().equals(dateInput) ) {
                    reportList.add(s);
                }
            }
            printAdminReportTable(reportList, "DATE: " + dateInput);
            break;
        }
    }

    public void handleFilterByMember() {
        List<WorkoutSchedule> reportList = new ArrayList<>();
        System.out.print("Enter Member's Name or Username to filter (or '0' to go back): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return;

        String searchKeyword = removeAccents(input).toLowerCase();

        for (WorkoutSchedule s : scheduleList) {
            String memberUser = s.getMemberUsername().toLowerCase();
            String memberFullName = removeAccents(getUserFullName(s.getMemberUsername())).toLowerCase();
            if (memberUser.contains(searchKeyword) || memberFullName.contains(searchKeyword)) {
                reportList.add(s);
            }
        }
        printAdminReportTable(reportList, "MEMBER SEARCH: " + input);
    }

    public void handleFilterByTrainer() {
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
        printAdminReportTable(reportList, "TRAINER SEARCH: " + input);
    }

    private void printAdminReportTable(List<WorkoutSchedule> list, String filterType) {
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

        System.out.println("\n-------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("ATTENDANCE REPORT FOR ADMIN [%s] - Total: %d records\n", filterType.toUpperCase(), list.size());
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-10s | %-6s | %-20s | %-12s | %-20s | %-12s | %-10s\n",
                "SCH-ID", "Date", "Time", "Member Name", "Member Username", "Trainer Name", "Trainer Username", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");

        for (WorkoutSchedule s : list) {
            String memberName = getUserFullName(s.getMemberUsername());
            String trainerName = getUserFullName(s.getTrainerUsername());

            if (trainerName.length() > 20) trainerName = trainerName.substring(0, 17) + "...";

            System.out.printf("%-8s | %-10s | %-6s | %-20s | %-12s | %-20s | %-12s | %-10s\n",
                    s.getScheduleId(), s.getDate(), s.getTime(), memberName, s.getMemberUsername(), trainerName, s.getTrainerUsername(), s.getProgressStatus());
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------------------");
    }

    private String getUserFullName(String username) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                return u.getFullName();
            }
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
}