package com.LiangLliu.utils.string.pinyin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PinYinUtilsTest {

    @Test
    public void should_converter_word_to_pinyin_when_given_hanzi() {
        String word = "我爱你";
        String except = "wo ai ni";
        assertEquals(except, PinYinUtils.convert(word));
    }

}
