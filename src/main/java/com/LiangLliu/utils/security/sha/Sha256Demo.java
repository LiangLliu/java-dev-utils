package com.LiangLliu.utils.security.sha;

import com.LiangLliu.utils.bytes.ByteUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class Sha256Demo {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        String str = "hello";

        byte[] hashbytes = messageDigest.digest(
                str.getBytes(StandardCharsets.UTF_8));

        Stream.of(hashbytes).forEach(System.out::print);
        System.out.println();
        System.out.println(hashbytes.length);
        String sha3Hex = ByteUtils.bytesToHex(hashbytes);
        System.out.println(sha3Hex);
        System.out.println(sha3Hex.length());

    }

}
/*

2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824

 */
