package com.archyx.slate.util;

public class LoreUtil {

    public static String getStyle(String input) {
        int firstColorCode = input.indexOf("&");
        int currentPos = firstColorCode;
        while (input.charAt(currentPos) == '&') {
            currentPos += 2;
        }
        return input.substring(firstColorCode, currentPos);
    }

}
