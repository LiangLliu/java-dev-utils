package com.LiangLliu.utils.math.decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalExample {

    public static void main(String[] args) {

        double input = 3.14159265359;
        System.out.println("double : " + input);

        BigDecimal bd = new BigDecimal(input).setScale(4, RoundingMode.HALF_UP);
        double salary = bd.doubleValue();

        System.out.println("salary : " + salary);
    }

}