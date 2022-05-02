package com.dfsek.substrate;

import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class NashornTest {

    @State(Scope.Benchmark)
    public static class NashornState {
        private CompiledScript script;
        private ScriptEngine engine;

        @Param({"4", "800", "3343"})
        public double value;
        @Setup
        public void setup() throws ScriptException {
            try {
                ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();

                String script = IOUtils.toString(NashornTest.class.getResource("/nashorn.js"), StandardCharsets.UTF_8);

                this.script = ((Compilable) engine).compile(script);
                this.engine = engine;

            } catch (IOException | ScriptException e) {
                throw new RuntimeException(e);
            }

            Bindings bindings = engine.createBindings();
            bindings.put("input", value);

            double result = (Double) script.eval(bindings);
            double actual = Math.sqrt(value);

            if (!Util.epsilonCompare(result, actual)) {
                throw new IllegalStateException("Expected " + actual + ", got " + result);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class NashornStaticState {
        private CompiledScript script;

        @Param({"4", "800", "3343"})
        public double value;
        @Setup
        public void setup() {
            try {
                ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();

                engine.getContext().setAttribute("input", value, ScriptContext.ENGINE_SCOPE);
                String script = IOUtils.toString(Objects.requireNonNull(NashornTest.class.getResource("/nashorn.js")), StandardCharsets.UTF_8);

                this.script = ((Compilable) engine).compile(script);

                double result = (Double) this.script.eval();
                double actual = Math.sqrt(value);

                if (!Util.epsilonCompare(result, actual)) {
                    throw new IllegalStateException("Expected " + actual + ", got " + result);
                }
            } catch (IOException | ScriptException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Benchmark
    public void nashornDynamicValue(NashornState state, Blackhole blackhole) throws ScriptException {
        Bindings bindings = state.engine.createBindings();
        bindings.put("input", state.value);

        blackhole.consume(state.script.eval(bindings));
    }

    @Benchmark
    public void nashornStaticValue(NashornStaticState state, Blackhole blackhole) throws ScriptException {
        blackhole.consume(((Double) state.script.eval()).doubleValue());
    }
}
