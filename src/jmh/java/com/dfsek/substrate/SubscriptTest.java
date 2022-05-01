package com.dfsek.substrate;

import com.dfsek.substrate.parser.Parser;
import org.apache.commons.io.IOUtils;
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

        @Setup
        public void setup() {
            try {
                script = new Parser<>(
                        IOUtils.toString(
                                Objects.requireNonNull(
                                        SubscriptTest.class.getResource(
                                                "/performance.sbsc"
                                        )
                                ),
                                Charset.defaultCharset()
                        ),
                        Input.class,
                        Output.class
                ).parse();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Param({"4", "800", "3343"})
        public double value;
    }

    @Benchmark
    public void subscriptDynamicValue(SubscriptState state, Blackhole blackhole) {
        double result = state.script.execute(new Input(state.value), null).out;

        if (!Util.epsilonCompare(result, Math.sqrt(state.value), 0.001)) {
            throw new IllegalStateException("Expected " + Math.sqrt(state.value) + ", got " + result);
        }
    }
}
