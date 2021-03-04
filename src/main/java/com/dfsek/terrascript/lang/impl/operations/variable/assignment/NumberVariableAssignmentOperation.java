package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.DSTORE;

public class NumberVariableAssignmentOperation extends VariableAssignmentOperation {

    public NumberVariableAssignmentOperation(Operation value) {
        super(value);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        value.apply(visitor, data);
        visitor.visitVarInsn(DSTORE, ((ScriptBuildData) data).getVarSize());
    }
}
