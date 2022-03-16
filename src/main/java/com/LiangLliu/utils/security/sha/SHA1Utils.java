package com.LiangLliu.utils.security.sha;


import com.LiangLliu.utils.bytes.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Utils {
    public static String generate(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        byte[] hashBytes = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));

        return ByteUtils.bytesToHex(hashBytes);
    }
}
