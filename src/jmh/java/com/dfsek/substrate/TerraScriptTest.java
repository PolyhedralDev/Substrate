package com.dfsek.substrate;


import com.dfsek.substrate.terrascript.GetInputFunction;
import com.dfsek.substrate.terrascript.SetResultFunction;
import com.dfsek.terra.addons.terrascript.parser.Parser;
import com.dfsek.terra.addons.terrascript.parser.lang.Block;
import com.dfsek.terra.addons.terrascript.parser.lang.ImplementationArguments;
import com.dfsek.terra.addons.terrascript.parser.lang.Returnable;
import com.dfsek.terra.addons.terrascript.parser.lang.functions.FunctionBuilder;
import com.dfsek.terra.addons.terrascript.script.builders.UnaryNumberFunctionBuilder;
import com.dfsek.terra.addons.terrascript.script.builders.ZeroArgFunctionBuilder;
import com.dfsek.terra.addons.terrascript.tokenizer.Position;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

public class TerraScriptTest {
    public static class Environment implements ImplementationArguments {
        private double result;
        private final double input;

        public Environment(double input) {
            this.input = input;
        }

        public void setResult(double result) {
            this.result = result;
        }

        public double getResult() {
            return result;
        }

        public double getInput() {
            return input;
        }
    }

    @State(Scope.Benchmark)
    public static class TerraScriptState {
        private Block script;

        @Param({"4", "800", "3343"})
        public double value;

        @Setup
        public void setup() {
            try {
                Parser parser = new Parser(
                        IOUtils.toString(
                                Objects.requireNonNull(TerraScriptTest.class.getResourceAsStream("/terrascript.tesf")
                                ),
                                Charset.defaultCharset()
                        )
                ).registerFunction("getInput", new FunctionBuilder<GetInputFunction>() {
                    @Override
                    public GetInputFunction build(List<Returnable<?>> argumentList, Position position) {
                        return new GetInputFunction(position);
                    }

                    @Override
                    public int argNumber() {
                        return 0;
                    }

                    @Override
                    public Returnable.ReturnType getArgument(int position) {
                        return null;
                    }
                }).registerFunction("setResult", new FunctionBuilder<SetResultFunction>() {
                    @Override
                    public SetResultFunction build(List<Returnable<?>> argumentList, Position position) {
                        return new SetResultFunction(position, (Returnable<Double>) argumentList.get(0));
                    }

                    @Override
                    public int argNumber() {
                        return 1;
                    }

                    @Override
                    public Returnable.ReturnType getArgument(int position) {
                        return switch (position) {
                            case 0 -> Returnable.ReturnType.NUMBER;
                            default -> null;
                        };
                    }
                });

                script = parser.parse();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Environment environment = new Environment(value);
            script.apply(environment);
            double result = environment.getResult();
            double actual = Math.sqrt(value);

            if (!Util.epsilonCompare(result, actual)) {
                throw new IllegalStateException("Expected " + actual + ", got " + result);
            }
        }
    }

    @Benchmark
    public void terraScriptDynamicValue(TerraScriptState state, Blackhole blackhole) {
        Environment environment = new Environment(state.value);
        state.script.apply(environment);
        blackhole.consume(environment.getResult());
    }
}
