package com.dfsek.substrate;

public class Util {
    public static boolean epsilonCompare(double v, double v2, double e) {
        return Math.abs(v - v2) <= e;
    }
}
