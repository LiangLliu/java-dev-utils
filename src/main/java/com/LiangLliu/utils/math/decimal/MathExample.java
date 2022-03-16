package com.LiangLliu.utils.math.decimal;

public class MathExample {

    public static void main(String[] args) {

        double input = 1205.6358;

        System.out.println("salary : " + input);

        double salary = Math.round(input * 100.0) / 100.00;

        System.out.println("salary : " + salary);

    }

}
