package com.LiangLliu.utils.security.sha;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SHA1UtilsTest {
    @Test
    public void should_get_SHA1_with_string_when_given_one_string() throws NoSuchAlgorithmException {
        String str = "hello";
        String except = "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d";

        String result = SHA1Utils.generate(str);
        assertEquals(except, result);
    }
}