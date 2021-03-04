package com.dfsek.terrascript.lang.impl.operations.variable.assignment;

import com.dfsek.terrascript.lang.impl.ScriptBuildData;
import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class StringVariableAssignmentOperation extends VariableAssignmentOperation {
    public StringVariableAssignmentOperation(Operation value, String id, Position position) {
        super(value, id, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        value.apply(visitor, data);
        if(((ScriptBuildData) data).getVariableType(id) != ScriptBuildData.VariableType.STRING) {
            throw new ParseException("Expected STR, found " + ((ScriptBuildData) data).getVariableType(id), position);
        }
        visitor.visitVarInsn(ASTORE, ((ScriptBuildData) data).getVariableIndex(id));
    }
}
