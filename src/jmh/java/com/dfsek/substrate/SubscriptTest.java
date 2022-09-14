package com.dfsek.substrate;

import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

public class SubscriptTest {
    public record Input(double input) {
    }

    public record Output(double out) {
    }

    @State(Scope.Benchmark)
    public static class SubscriptState {
        private Script<Input, Output> script;

        @Param({"4", "800", "3343"})
        public double value;

        @Setup
        public void setup() {
            try {
                script = new Parser<>(
                        Input.class,
                        Output.class
                ).parse(IOUtils.toString(
                        Objects.requireNonNull(
                                SubscriptTest.class.getResource(
                                        "/performance.sbsc"
                                )
                        ),
                        Charset.defaultCharset()
                ));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            double result = script.execute(new Input(value), null).out;
            double actual = Math.sqrt(value);

            if (!Util.epsilonCompare(result, actual)) {
                throw new IllegalStateException("Expected " + actual + ", got " + result);
            }
        }
    }

    @Benchmark
    public void subscriptDynamicValue(SubscriptState state, Blackhole blackhole) {
        blackhole.consume(state.script.execute(new Input(state.value), null).out);
    }
}
