package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class BooleanVariableAssignmentOperation extends VariableAssignmentOperation {
    public BooleanVariableAssignmentOperation(Operation value, String id, Position position) {
        super(value, id, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        value.apply(visitor, data);
        if(((ScriptBuildData) data).getVariableType(id) != ReturnType.BOOL) {
            throw new ParseException("Expected BOOL, found " + ((ScriptBuildData) data).getVariableType(id), position);
        }
        visitor.visitVarInsn(ISTORE, ((ScriptBuildData) data).getVariableIndex(id));
    }
}
