package com.dfsek.substrate.lang.impl.operations.comparison.string;

import com.dfsek.substrate.lang.impl.operations.NotOperation;
import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class NotEqualsOperationString extends StringComparisonOperation {
    public NotEqualsOperationString(Operation left, Operation right, Position position) {
        super(left, right, position);
    }

    @Override
    public void applyOperation(MethodVisitor visitor, BuildData data) throws ParseException {
        visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        new NotOperation(position).apply(visitor, data);
    }
}
