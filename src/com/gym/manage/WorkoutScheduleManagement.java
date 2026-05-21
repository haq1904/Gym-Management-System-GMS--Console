package com.gym.manage;

import com.gym.model.schedule.WorkoutSchedule;
import com.gym.model.users.Member;
import com.gym.model.users.User;
import com.gym.repository.GymContext;
import com.gym.repository.IRepository;


import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class WorkoutScheduleManagement {
    private Scanner scanner = new Scanner(System.in);
    private List<WorkoutSchedule> scheduleList;
    private IRepository<WorkoutSchedule> scheduleRepo;
    private List<User> userList;

    public WorkoutScheduleManagement(GymContext context){
        scheduleList = context.getSchedulesList();
        scheduleRepo = context.getScheduleRepo();
        userList = context.getUserList();
    }

    public void createAndAssignSchedule(String trainerUsername) {
        while (true) {
            System.out.println("\n[ CREATE NEW SCHEDULE ]");

            String memberUsername = "";
            Member foundMember = null;

            // --- BƯỚC 1: VÒNG LẶP NHẬP VÀ KIỂM TRA USERNAME ---
            while (true) {
                System.out.print("Enter Member's Full Name to search (or '0' to exit to Trainer Menu): ");
                String searchName = scanner.nextLine().trim();

                // LỐI THOÁT HIỂM SỐ 1
                if (searchName.equals("0")) {
                    System.out.println("[ INFO ] Exiting Schedule Creation. Returning to Menu...");
                    return;
                }

                Map<Integer, Member> searchResults = new HashMap<>();
                int stt = 1;

                String normalizedSearchName = removeAccents(searchName).toLowerCase();

                for (User u : userList) {
                    if (u instanceof Member) {
                        // LỘT DẤU TÊN CỦA USER TRONG DANH SÁCH
                        String normalizedFullName = removeAccents(u.getFullName()).toLowerCase();

                        // SO SÁNH 2 CHUỖI ĐÃ SẠCH DẤU (Gõ "ho" tự nhận "Hồ", "Hố", "Hổ")
                        if (normalizedFullName.contains(normalizedSearchName)) {
                            searchResults.put(stt, (Member) u);
                            stt++;
                        }
                    }
                }

                // 1.3 Xử lý trường hợp không tìm thấy ai
                if (searchResults.isEmpty()) {
                    System.out.println("[ ERROR ] No members found matching name: '" + searchName + "'. Please try again.");
                    continue; // Quay lại bắt nhập lại tên
                }

                // 1.4 In bảng kết quả tìm kiếm cho HLV chọn (Hiển thị thêm Gói tập và Trạng thái)
                System.out.println("\n--- SEARCH RESULTS ---");
                System.out.printf("%-5s | %-25s | %-20s | %-12s\n", "STT", "Full Name", "Membership", "Status");
                System.out.println("------------------------------------------------------------------------");

                for (Map.Entry<Integer, Member> entry : searchResults.entrySet()) {
                    Member m = entry.getValue();
                    // Lưu ý: Đảm bảo class Member của ông có 2 hàm getMembershipType() và getStatus() nhé!
                    System.out.printf("%-5d | %-25s | %-20s | %-12s\n",
                            entry.getKey(),
                            m.getFullName(),
                            m.getMembershipType(),
                            m.getSubscriptionStatus()
                            // Lấy trạng thái (VD: Active)
                    );
                }
                System.out.println("------------------------------------------------------------------------");

                // 1.5 Vòng lặp yêu cầu HLV chọn đúng STT
                boolean isSelectionValid = false;
                while (!isSelectionValid) {
                    System.out.print("Enter STT to select a member (or '0' to search another name): ");
                    String sttInput = scanner.nextLine().trim();

                    if (sttInput.equals("0")) {
                        System.out.println("[ INFO ] Canceling selection...");
                        break; // Phá vòng lặp chọn STT, quay lại đầu vòng lặp nhập Tên
                    }

                    try {
                        int selectedStt = Integer.parseInt(sttInput);

                        // Kiểm tra xem STT nhập vào có tồn tại trong Map không
                        if (searchResults.containsKey(selectedStt)) {
                            // 1.6 Lấy ra đối tượng Member tương ứng và trích xuất Username NGẦM
                            foundMember = searchResults.get(selectedStt);
                            memberUsername = foundMember.getUsername(); // HLV không hề biết cái Username này
                            isSelectionValid = true;
                        } else {
                            System.out.println("[ ERROR ] Invalid STT. Please select a number from the list above.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[ ERROR ] Please enter a valid number.");
                    }
                }

                // Nếu đã chọn thành công Member hợp lệ, phá vòng lặp tìm kiếm để đi xuống phần Ngày/Giờ
                if (isSelectionValid) {
                    break;
                }
            }

            String date = "";
            String time = "";

            while (true) {
                // 2.1 Nhập ngày
                while (true) {
                    System.out.print("Enter Date (dd/MM/yyyy) or '0' to back to menu: ");
                    date = scanner.nextLine().trim();

                    if (date.equals("0")) {
                        System.out.println("[ INFO ] Canceled current input. Restarting...");
                        return; // Nhảy vọt lên quay lại từ đầu vòng lặp lớn!
                    }

                    if (date.matches("^[0-9]{2}/[0-9]{2}/[0-9]{4}$")) {
                        break;
                    }
                    System.out.println("[ ERROR ] Invalid Date format! Please use dd/MM/yyyy (e.g., 25/05/2026).");
                }

                // 2.2 Nhập giờ
                while (true) {
                    System.out.print("Enter Time (HH:mm) or '0' to back to menu: ");
                    time = scanner.nextLine().trim();

                    // LỐI THOÁT HIỂM SỐ 3: Hủy ngang để làm lại từ đầu
                    if (time.equals("0")) {
                        System.out.println("[ INFO ] Canceled current input. Restarting...");
                        return;
                    }

                    if (time.matches("^[0-9]{2}:[0-9]{2}$")) {
                        break;
                    }
                    System.out.println("[ ERROR ] Invalid Time format! Please use HH:mm (e.g., 14:30).");
                }

                // 2.3 Check trùng lịch
                String conflictError = checkScheduleConflict(trainerUsername, memberUsername, date, time);
                if (conflictError != null) {
                    System.out.println(conflictError);
                    System.out.println("[ INFO ] Please choose a different Date or Time.");
                    continue;
                }

                break;
            }

            // --- BƯỚC 4: VÒNG LẶP NHẬP BÀI TẬP ---
            String exercises = "";
            while (true) {
                System.out.print("Enter Exercises (separated by '|', e.g., Yoga|Cardio) or '0' to restart: ");
                exercises = scanner.nextLine().trim();

                // LỐI THOÁT HIỂM SỐ 4: Hủy ngang để làm lại từ đầu
                if (exercises.equals("0")) {
                    System.out.println("[ INFO ] Canceled current input. Restarting...");
                    return;
                }

                if (!exercises.isEmpty()) {
                    break;
                }
                System.out.println("[ ERROR ] Exercises list cannot be empty!");
            }
            String newScheduleId = generateScheduleId(scheduleList);
            String progressStatus = "Not Started";

            WorkoutSchedule newSchedule = new WorkoutSchedule(newScheduleId, memberUsername, trainerUsername, date, time, exercises, progressStatus);
            scheduleRepo.add(scheduleList, newSchedule);
            System.out.println("[ SUCCESS ] Schedule assigned to " + foundMember.getFullName() + " successfully!");
            System.out.println("---------------------------------------------------");

        }
    }

    private String checkScheduleConflict(String trainerUsername, String memberUsername, String date, String time) {
        for (WorkoutSchedule s : scheduleList) {
            if (s.getDate().equals(date) && s.getTime().equals(time)) {
                if (s.getMemberUsername().equals(memberUsername)) {
                    return "[ ERROR ] Scheduling Conflict: Member '" + memberUsername + "' is already scheduled for another workout at " + time + " on " + date + "!";
                }
            }
        }
        return null;
    }

    public void viewTrainerSchedules(String trainerUsername) {
        System.out.println("\n=======================================================");
        System.out.println("       --- MY ASSIGNED WORKOUT SCHEDULES ---           ");
        System.out.println("=======================================================");

        List<WorkoutSchedule> trainerSchedules = new ArrayList<>();
        for (WorkoutSchedule s : scheduleList) {
            if (s.getTrainerUsername().equals(trainerUsername)) {
                trainerSchedules.add(s);
            }
        }

        if (trainerSchedules.isEmpty()) {
            System.out.println("[ INFO ] You haven't assigned any schedules yet.");
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        trainerSchedules.sort((s1, s2) ->
                LocalTime.parse(s1.getTime(), timeFormatter).compareTo(LocalTime.parse(s2.getTime(), timeFormatter))
        );

        List<String> uniqueDates = new ArrayList<>();
        for (WorkoutSchedule s : trainerSchedules) {
            if (!uniqueDates.contains(s.getDate())) {
                uniqueDates.add(s.getDate());
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        uniqueDates.sort((d1, d2) -> LocalDate.parse(d1, formatter).compareTo(LocalDate.parse(d2, formatter)));

        for (String date : uniqueDates) {
            System.out.println("\nDATE: " + date);
            System.out.println("------------------------------------------------------------------------------------------------");
            // In tiêu đề cột dạng bảng cho đẹp mắt và dễ nhìn
            System.out.printf("%-10s | %-12s | %-25s | %-25s | %-12s\n", "ID", "Time", "Member", "Exercises", "Status");
            System.out.println("------------------------------------------------------------------------------------------------");

            // Quét danh sách lịch của Trainer xem dòng nào khớp với ngày đang xét thì in ra
            for (WorkoutSchedule s : trainerSchedules) {
                if (s.getDate().equals(date)) {
                    String formattedExercises = s.getExercises().replace("|", ", ");

                    if (formattedExercises.length() > 23) {
                        formattedExercises = formattedExercises.substring(0, 20) + "...";
                    }
                    String memberDisplayName = "";
                    for (User u : userList) {
                        if (u.getUsername().equals(s.getMemberUsername())) {
                            memberDisplayName = u.getFullName();
                            break;
                        }
                    }

                    System.out.printf("%-10s | %-12s | %-25s | %-25s | %-12s\n",
                            s.getScheduleId(),
                            s.getTime(),
                            memberDisplayName,
                            formattedExercises,
                            s.getProgressStatus()
                    );
                }
            }
            System.out.println("------------------------------------------------------------------------------------------------");
        }
    }

    public void viewMemberSchedules(List<WorkoutSchedule> scheduleList, String memberUsername) {
        System.out.println("\n--- MY SCHEDULES (" + memberUsername + ") ---");
        int count = 0;
        for (WorkoutSchedule s : scheduleList) {
            if (s.getMemberUsername().equalsIgnoreCase(memberUsername)) {
                printScheduleInfo(s);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("[ INFO ] You don't have any workout schedules yet.");
        }
    }

    public void updateProgress(List<WorkoutSchedule> scheduleList, IRepository<WorkoutSchedule> scheduleRepo, String memberUsername) {
        System.out.println("\n[ UPDATE PROGRESS ]");
        System.out.print("Enter Schedule ID to update (e.g., SCH001): ");
        String targetId = scanner.nextLine().trim();

        WorkoutSchedule targetSchedule = null;

        for (WorkoutSchedule s : scheduleList) {
            if (s.getScheduleId().equalsIgnoreCase(targetId) && s.getMemberUsername().equalsIgnoreCase(memberUsername)) {
                targetSchedule = s;
                break;
            }
        }

        if (targetSchedule == null) {
            System.out.println("[ ERROR ] Schedule ID not found or it does not belong to you!");
        } else {
            System.out.println("Current Status: " + targetSchedule.getProgressStatus());
            System.out.println("1. Not Started | 2. In Progress | 3. Completed | 4. Absent");
            System.out.print("-> Select new status (1-4): ");
            String statusChoice = scanner.nextLine().trim();

            String newStatus = "";
            if (statusChoice.equals("1")) newStatus = "Not Started";
            else if (statusChoice.equals("2")) newStatus = "In Progress";
            else if (statusChoice.equals("3")) newStatus = "Completed";
            else if (statusChoice.equals("4")) newStatus = "Absent";
            else {
                System.out.println("[ WARNING ] Invalid choice. Update canceled.");
                return;
            }

            targetSchedule.setProgressStatus(newStatus);
            scheduleRepo.saveData(scheduleList);
            System.out.println("[ SUCCESS ] Progress updated to: " + newStatus);
        }
    }

    private void printScheduleInfo(WorkoutSchedule s) {
        System.out.println("----------------------------------------");
        System.out.println("ID       : " + s.getScheduleId());
        System.out.println("Trainer  : " + s.getTrainerUsername());
        System.out.println("Member   : " + s.getMemberUsername());
        System.out.println("Date/Time: " + s.getDate() + " at " + s.getTime());
        System.out.println("Exercises: " + s.getExercises().replace("|", ", "));
        System.out.println("Status   : " + s.getProgressStatus());
    }

    private String generateScheduleId(List<WorkoutSchedule> scheduleList) {
        int maxId = 0;
        for (WorkoutSchedule s : scheduleList) {
            String currentId = s.getScheduleId();
            if (currentId.toUpperCase().startsWith("SCH")) {
                try {
                    int number = Integer.parseInt(currentId.substring(3));
                    if (number > maxId) {
                        maxId = number;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua
                }
            }
        }
        return String.format("SCH%03d", maxId + 1);
    }

    private String removeAccents(String str) {
        if (str == null) return null;

        // 1. Phân tách các ký tự có dấu thành ký tự gốc + dấu (VD: 'ễ' -> 'e' + '~' + '^')
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);

        // 2. Dùng Regex quét sạch các cái dấu vừa bị tách ra
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");

        // 3. Xử lý nốt "đặc sản" chữ Đ của tiếng Việt
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }
}