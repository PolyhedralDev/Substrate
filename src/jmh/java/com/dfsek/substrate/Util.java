package com.dfsek.substrate;

public class Util {

    public static final double EPSILON = 0.001;
    public static boolean epsilonCompare(double v, double v2) {
        return Math.abs(v - v2) <= EPSILON;
    }
}
