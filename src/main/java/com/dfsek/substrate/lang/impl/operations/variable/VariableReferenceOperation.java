package com.dfsek.substrate.lang.impl.operations.variable;

import com.dfsek.substrate.lang.impl.ScriptBuildData;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class VariableReferenceOperation implements Operation {
    private final Position position;
    private final String id;
    private final ReturnType expect;

    public VariableReferenceOperation(Position position, String id, ReturnType expect) {
        this.position = position;
        this.id = id;
        this.expect = expect;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        ReturnType type = ((ScriptBuildData) data).getVariableType(id);

        int index = ((ScriptBuildData) data).getVariableIndex(id);

        switch(type) {
            case NUM: {
                visitor.visitVarInsn(DLOAD, index);
                break;
            }
            case BOOL: {
                visitor.visitVarInsn(ILOAD, index);
                break;
            }
            case STR: {
                visitor.visitVarInsn(ALOAD, index);
                break;
            }
            default: throw new ParseException("Unexpected type: " + type, position);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType getType() {
        return expect;
    }
}
