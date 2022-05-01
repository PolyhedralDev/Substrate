package com.dfsek.substrate;

import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NashornTest {

    @State(Scope.Benchmark)
    public static class NashornState {
        private CompiledScript script;
        private ScriptEngine engine;

        @Setup
        public void setup() {
            try {
                ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine();

                String script = IOUtils.toString(NashornTest.class.getResource("/nashorn.js"), StandardCharsets.UTF_8);

                this.script = ((Compilable) engine).compile(script);
                this.engine = engine;

            } catch (IOException | ScriptException e) {
                throw new RuntimeException(e);
            }
        }

        @Param({"4", "800", "3343"})
        public double value;
    }

    @Benchmark
    public void bench(NashornState state, Blackhole blackhole) throws ScriptException {
        Bindings bindings = state.engine.createBindings();
        bindings.put("input", state.value);

        double result = (Double) state.script.eval(bindings);

        if (!Util.epsilonCompare(result, Math.sqrt(state.value), 0.001)) {
            throw new IllegalStateException("Expected " + Math.sqrt(state.value) + ", got " + result);
        }
    }
}
