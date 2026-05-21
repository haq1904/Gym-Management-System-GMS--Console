package com.gym.manage;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Helper {

    static public String removeAccents(String str) {
        if (str == null) return null;
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }
}
