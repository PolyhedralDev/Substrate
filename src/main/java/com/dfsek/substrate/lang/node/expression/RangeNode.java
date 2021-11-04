package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RangeNode extends ExpressionNode {
    private final ExpressionNode lower;
    private final ExpressionNode upper;

    private final Position position;

    public RangeNode(ExpressionNode lower, ExpressionNode upper, Position position) {
        this.lower = lower;
        this.upper = upper;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        upper.apply(visitor, data);

        lower.apply(visitor, data);
        visitor.visitInsn(DUP);

        int lowerRef = data.getOffset();
        data.offsetInc(1);
        visitor.visitVarInsn(ISTORE, lowerRef);

        visitor.visitInsn(ISUB);

        visitor.visitInsn(DUP);
        int totalRef = data.getOffset();
        data.offsetInc(1);
        visitor.visitVarInsn(ISTORE, totalRef);


        visitor.visitIntInsn(NEWARRAY, T_INT); // [ARef]

        visitor.visitInsn(ICONST_0); // [ARef, i]

        Label start = new Label();
        Label end = new Label();

        visitor.visitLabel(start);

        visitor.visitInsn(DUP); // [ARef, i, i]
        visitor.visitVarInsn(ILOAD, totalRef);
        visitor.visitJumpInsn(IF_ICMPGE, end); // [ARef, i]

        visitor.visitInsn(DUP2); // [ARef, i, ARef, i]
        visitor.visitInsn(DUP); // [ARef, i, ARef, i, i]

        visitor.visitVarInsn(ILOAD, lowerRef); // [ARef, i, ARef, i, i, size]
        visitor.visitInsn(IADD); // [ARef, i, ARef, i, n]

        visitor.visitInsn(IASTORE); // [ARef, i]

        visitor.visitInsn(ICONST_1); // [ARef, i, 1]
        visitor.visitInsn(IADD); // [ARef, i]


        visitor.visitJumpInsn(GOTO, start);

        visitor.visitLabel(end);
        visitor.visitInsn(POP); // [ARef]
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature referenceType(BuildData data) {
        return Signature.list().applyGenericReturn(0, Signature.integer());
    }
}
