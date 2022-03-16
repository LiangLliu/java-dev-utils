package com.LiangLliu.utils.security.base64;

import java.util.Base64;

public class Base64Utils {

    public static String encode(String str) {
        byte[] encodedBytes = Base64.getEncoder().encode(str.getBytes());
        return new String(encodedBytes);
    }

    public static String decode(String str) {
        byte[] encodedBytes = Base64.getDecoder().decode(str.getBytes());
        return new String(encodedBytes);
    }
}
