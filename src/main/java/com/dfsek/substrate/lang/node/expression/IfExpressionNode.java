package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class IfExpressionNode extends ExpressionNode {
    private final ExpressionNode predicate;
    private final ExpressionNode caseTrue;
    private final ExpressionNode caseFalse;

    public IfExpressionNode(ExpressionNode predicate, ExpressionNode caseTrue, ExpressionNode caseFalse) {
        this.predicate = predicate;
        this.caseTrue = caseTrue;
        this.caseFalse = caseFalse;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(!predicate.returnType(data).equals(Signature.bool())) {
            throw new ParseException("If expression predicate must return BOOL, got " + predicate.returnType(data), predicate.getPosition());
        }
        if(!caseTrue.returnType(data).equals(caseFalse.returnType(data))) {
            throw new ParseException("If expression case has invalid return type, expected " + caseTrue.returnType(data) + ", got " + caseFalse.returnType(data), caseFalse.getPosition());
        }

        Label equal = new Label();
        Label end = new Label();
        predicate.apply(visitor, data);

        visitor.visitJumpInsn(IFEQ, equal);

        caseTrue.apply(visitor, data);

        visitor.visitJumpInsn(GOTO, end);

        visitor.visitLabel(equal);

        caseFalse.apply(visitor, data);

        visitor.visitLabel(end);
    }

    @Override
    public Position getPosition() {
        return predicate.getPosition();
    }

    @Override
    public Signature returnType(BuildData data) {
        return caseTrue.returnType(data);
    }
}
