package com.dfsek.terrascript.lang.impl.operations.looplike;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class IfOperation implements Operation {
    private final Operation conditional;
    private final Operation statement;

    public IfOperation(Operation conditional, Operation statement) {
        this.conditional = conditional;
        this.statement = statement;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }
}
