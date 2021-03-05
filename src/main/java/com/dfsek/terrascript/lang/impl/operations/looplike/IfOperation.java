package com.dfsek.terrascript.lang.impl.operations.looplike;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class IfOperation implements Operation, BlockedOperation {
    private final Operation conditional;
    private final Operation statement;
    private final Position position;

    public IfOperation(Operation conditional, Operation statement, Position position) {
        this.conditional = conditional;
        this.statement = statement;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(conditional.getType() != ReturnType.BOOL) throw new ParseException("If statement conditional must be of type BOOL.", conditional.getPosition());

        Label end = new Label();

        conditional.apply(visitor, data);

        visitor.visitJumpInsn(IFEQ, end);

        statement.apply(visitor, data);

        visitor.visitLabel(end);
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
