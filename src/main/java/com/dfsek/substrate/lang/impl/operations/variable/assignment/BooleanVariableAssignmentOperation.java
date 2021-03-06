package com.dfsek.substrate.lang.impl.operations.variable.assignment;

import com.dfsek.substrate.lang.impl.ScriptBuildData;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
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
