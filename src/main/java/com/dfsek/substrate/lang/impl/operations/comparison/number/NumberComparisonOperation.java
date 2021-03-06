package com.dfsek.substrate.lang.impl.operations.comparison.number;

import com.dfsek.substrate.lang.impl.operations.comparison.ComparisonOperation;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public abstract class NumberComparisonOperation extends ComparisonOperation {
    protected NumberComparisonOperation(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        System.out.println(left);
        System.out.println(right);
        if(left.getType() != ReturnType.NUM) throw new ParseException("Expected NUM, found " + left.getType(), left.getPosition());
        if(right.getType() != ReturnType.NUM) throw new ParseException("Expected NUM, found " + right.getType(), right.getPosition());

        left.apply(visitor, data);
        right.apply(visitor, data);
        applyOperation(visitor, data);
    }

    public abstract void applyOperation(MethodVisitor visitor, BuildData data) throws ParseException;
}
