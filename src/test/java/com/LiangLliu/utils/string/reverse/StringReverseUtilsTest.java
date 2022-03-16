package com.LiangLliu.utils.string.reverse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringReverseUtilsTest {

    @Test
    public void should_reverse_string_when_given_one_string() {
        String str = "1234567890";

        String except = "0987654321";

        assertEquals(except, StringReverseUtils.reverse1(str));
        assertEquals(except, StringReverseUtils.reverse2(str));
        assertEquals(except, StringReverseUtils.reverse3(str));
    }
}
