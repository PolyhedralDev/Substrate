package com.dfsek.terrascript.lang.impl.operations.number.binary;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public abstract class BinaryNumberOperation implements Operation {
    private final Operation left;
    private final Operation right;

    private final Position position;

    protected BinaryNumberOperation(Operation left, Operation right, Position position) {
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
    public Position getPosition() {
        return position;
    }

    @Override
    public ReturnType getType() {
        return ReturnType.NUM;
    }
}
