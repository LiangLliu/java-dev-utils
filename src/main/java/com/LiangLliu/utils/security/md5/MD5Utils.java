package com.LiangLliu.utils.security.md5;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String generate(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(str.getBytes());
        byte[] digest = messageDigest.digest();
        return new BigInteger(1, digest).toString(16).toUpperCase();
    }

}
