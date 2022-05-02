package com.dfsek.substrate.terrascript;

import com.dfsek.substrate.TerraScriptTest;
import com.dfsek.terra.addons.terrascript.parser.lang.ImplementationArguments;
import com.dfsek.terra.addons.terrascript.parser.lang.Returnable;
import com.dfsek.terra.addons.terrascript.parser.lang.Scope;
import com.dfsek.terra.addons.terrascript.parser.lang.functions.Function;
import com.dfsek.terra.addons.terrascript.tokenizer.Position;

public final class SetResultFunction implements Function<Void> {
    private final Position position;
    private final Returnable<Double> arg;

    public SetResultFunction(Position position, Returnable<Double> arg) {
        this.position = position;
        this.arg = arg;
    }

    @Override
    public Void apply(ImplementationArguments implementationArguments, Scope scope) {
        ((TerraScriptTest.Environment) implementationArguments).setResult(arg.apply(implementationArguments, scope));
        return null;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType returnType() {
        return ReturnType.VOID;
    }
}