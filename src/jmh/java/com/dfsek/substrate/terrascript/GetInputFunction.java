package com.dfsek.substrate.terrascript;

import com.dfsek.substrate.TerraScriptTest;
import com.dfsek.terra.addons.terrascript.parser.lang.ImplementationArguments;
import com.dfsek.terra.addons.terrascript.parser.lang.Returnable;
import com.dfsek.terra.addons.terrascript.parser.lang.Scope;
import com.dfsek.terra.addons.terrascript.parser.lang.functions.Function;
import com.dfsek.terra.addons.terrascript.tokenizer.Position;

public final class GetInputFunction implements Function<Number> {
    private final Position position;

    public GetInputFunction(Position position) {
        this.position = position;
    }

    @Override
    public Double apply(ImplementationArguments implementationArguments, Scope scope) {
        return ((TerraScriptTest.Environment) implementationArguments).getInput();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType returnType() {
        return ReturnType.NUMBER;
    }
}