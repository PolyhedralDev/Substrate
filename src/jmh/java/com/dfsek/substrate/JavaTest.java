package com.dfsek.substrate;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

public class JavaTest {
    @State(Scope.Benchmark)
    public static class JavaState {

        @Param({"4", "800", "3343"})
        public double value;

        @Setup
        public void setup() {

            double result = squareRoot(value, Util.EPSILON);
            double actual = Math.sqrt(value);

            if (!Util.epsilonCompare(result, actual)) {
                throw new IllegalStateException("Expected " + actual + ", got " + result);
            }
        }
    }

    @Benchmark
    public void javaDynamicValue(JavaState state, Blackhole blackhole) {
        blackhole.consume(squareRoot(state.value, Util.EPSILON));
    }

    private static double squareRoot(double input, double epsilon) {
        double x = input;
        double root;

        while (true) {
            root = 0.5 * (x + (input / x));
            double diff = Math.abs(root - x);
            if (diff < epsilon) break;
            x = root;
        }
        return root;
    }
}
