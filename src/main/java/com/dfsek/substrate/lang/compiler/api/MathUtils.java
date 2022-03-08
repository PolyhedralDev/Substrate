package com.dfsek.substrate.lang.compiler.api;

public final class MathUtils {
    private MathUtils() {
        throw new IllegalStateException();
    }

    public static double pow2(double a) {
        return a * a;
    }

    public static double fastCeil(double f) {
        long i = (long) f;
        if (i < f) i++;
        return i;
    }

    public static double fastFloor(double f) {
        return f >= 0 ? (long) f : (long) f - 1;
    }

    public static double intPow(double x, double yd) {
        long y = (long) yd;
        double result = 1;
        while (y > 0) {
            if ((y & 1) == 0) {
                x *= x;
                y >>>= 1;
            } else {
                result *= x;
                y--;
            }
        }
        return result;
    }
}
