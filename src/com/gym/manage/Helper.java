package com.gym.manage;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

public class Helper {

    static public String removeAccents(String str) {
        if (str == null) return null;
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }

    public static String generateNextId(String prefix, List<String> existingIds, String formatStr) {
        int maxNumber = 0;

        for (String id : existingIds) {
            // Nếu ID bắt đầu bằng prefix (không phân biệt hoa thường)
            if (id != null && id.toLowerCase().startsWith(prefix.toLowerCase())) {
                try {
                    // Cắt bỏ phần chữ, chỉ lấy phần số đằng sau để ép kiểu
                    String numPart = id.substring(prefix.length());
                    int num = Integer.parseInt(numPart);

                    // Tìm số lớn nhất hiện tại
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua những ID người dùng tự đặt không có số ở đuôi (VD: "memberVIP")
                }
            }
        }

        // Trả về ID mới bằng cách cộng 1 vào số lớn nhất
        return prefix + String.format(formatStr, maxNumber + 1);
    }
}
