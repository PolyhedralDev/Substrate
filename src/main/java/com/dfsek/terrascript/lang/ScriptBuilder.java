package com.dfsek.terrascript.lang;

import com.dfsek.terrascript.ImplementationArguments;
import com.dfsek.terrascript.TerraScript;
import com.dfsek.terrascript.lang.impl.operations.IdOperation;
import com.dfsek.terrascript.lang.internal.Operation;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuilder {
    private final List<Operation> ops = new ArrayList<>();

    public void addOperation(Operation op) {
        ops.add(op);
    }

    public TerraScript build() {
        return new TerraScript() {
            @Override
            public void execute(ImplementationArguments implementationArguments) {
                System.out.println(((IdOperation) ops.get(0)).getId());
            }
        };
    }
}
