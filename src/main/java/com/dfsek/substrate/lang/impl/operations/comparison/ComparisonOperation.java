package com.dfsek.substrate.lang.impl.operations.comparison;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public abstract class ComparisonOperation implements Operation {
    private final Operation left;
    private final Operation right;
    private final Position position;

    protected ComparisonOperation(Operation left, Operation right, Position position) {
        this.left = left;
        this.right = right;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        left.apply(visitor, data);
        right.apply(visitor, data);
        applyOperation(visitor, data);
    }

    public abstract void applyOperation(MethodVisitor visitor, BuildData data);

    @Override
    public ReturnType getType() {
        return ReturnType.BOOL;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
