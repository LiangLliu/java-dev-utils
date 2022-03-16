package com.LiangLliu.utils.security.password;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class CommonsTextRandomPassUtilsTest {
    @Test
    public void should_generate_password() {

        Stream.iterate(16, i -> i + 1)
                .limit(100)
                .forEach(it -> System.out.println(CommonsTextRandomPassUtils.generateCommonTextPassword()));

        System.out.println(CommonsTextRandomPassUtils.generateCommonTextPassword());
    }

}