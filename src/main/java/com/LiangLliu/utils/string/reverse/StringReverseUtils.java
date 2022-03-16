package com.LiangLliu.utils.string.reverse;

public class StringReverseUtils {

    public static String reverse1(String str) {
        return new StringBuffer(str)
                .reverse()
                .toString();
    }

    public static String reverse2(String str) {
        int length = str.length();
        if (length <= 1) {
            return str;
        }
        String left = str.substring(0, length / 2);
        String right = str.substring(length / 2, length);
        return reverse2(right) + reverse2(left);
    }

    public static String reverse3(String str) {

        StringBuilder reverse = new StringBuilder();
        for (char c : str.toCharArray()) {
            reverse.insert(0, c);
        }
        return reverse.toString();
    }

}
