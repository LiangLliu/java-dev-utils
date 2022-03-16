package com.LiangLliu.utils.security.base64;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Base64UtilsTest {

    @Test
    public void should_return_string_with_base64_when_given_one_string() {
        String str = "1234";

        String except = "MTIzNA==";
        assertEquals(except, Base64Utils.encode(str));
    }

    @Test
    public void should_return_string_when_given_one_string_with_base64_encode() {
        String str = "MTIzNA==";
        String except = "1234";

        assertEquals(except, Base64Utils.decode(str));
    }

}