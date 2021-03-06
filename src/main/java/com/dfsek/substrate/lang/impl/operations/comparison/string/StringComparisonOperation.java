package com.dfsek.substrate.lang.impl.operations.comparison.string;

import com.dfsek.substrate.lang.impl.operations.comparison.ComparisonOperation;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public abstract class StringComparisonOperation extends ComparisonOperation {
    protected StringComparisonOperation(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(left.getType() != ReturnType.STR) throw new ParseException("Expected STR, found " + left.getType(), left.getPosition());
        if(right.getType() != ReturnType.STR) throw new ParseException("Expected STR, found " + right.getType(), right.getPosition());
        left.apply(visitor, data);
        right.apply(visitor, data);
        applyOperation(visitor, data);
    }

    public abstract void applyOperation(MethodVisitor visitor, BuildData data) throws ParseException;
}
