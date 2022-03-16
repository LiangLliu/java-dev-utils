package com.LiangLliu.utils.security.md5;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5UtilsTest {
    @Test
    public void should_get_md5_with_string_when_given_one_string() throws NoSuchAlgorithmException {
        String str = "testqawerdwqwe";
        String except = "8a7a385e440436cff98c4bbe2c19658e";

        String result = MD5Utils.generate(str);
        assertEquals(except.toUpperCase(), result);
    }
}