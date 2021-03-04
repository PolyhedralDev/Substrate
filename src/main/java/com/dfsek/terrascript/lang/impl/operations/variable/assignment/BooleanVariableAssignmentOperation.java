package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

public class BooleanVariableAssignmentOperation extends VariableAssignmentOperation {
    public BooleanVariableAssignmentOperation(Operation value, String id) {
        super(value, id);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        value.apply(visitor, data);
        visitor.visitVarInsn(ISTORE, ((ScriptBuildData) data).getVariableIndex(id));
    }
}
