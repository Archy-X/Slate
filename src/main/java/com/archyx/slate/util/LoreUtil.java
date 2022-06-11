package com.archyx.slate.util;

public class LoreUtil {

    public static String getStyle(String input) {
        int firstColorCode = input.indexOf("§");
        if (firstColorCode != -1) {
            int currentPos = firstColorCode;
            while (input.charAt(currentPos) == '§') {
                currentPos += 2;
            }
            return input.substring(firstColorCode, currentPos);
        } else {
            return "";
        }
    }

}
