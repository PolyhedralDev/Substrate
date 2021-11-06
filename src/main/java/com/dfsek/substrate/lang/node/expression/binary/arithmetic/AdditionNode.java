package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.BinaryOperationNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class AdditionNode extends BinaryOperationNode {
    public AdditionNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void applyOp(MethodVisitor visitor, BuildData data) {
        Signature leftType = ParserUtil.checkType(left, data, Signature.integer(), Signature.decimal(), Signature.string()).reference(data);
        ParserUtil.checkType(right, data, Signature.integer(), Signature.decimal(), Signature.string()).reference(data);

        ParserUtil.checkType(right, data, leftType);

        if(leftType.equals(Signature.integer())) {
            visitor.visitInsn(IADD);
        } else if(leftType.equals(Signature.decimal())) {
            visitor.visitInsn(DADD);
        } else if(leftType.equals(Signature.string())) {
            visitor.visitMethodInsn(INVOKEVIRTUAL,
                    "java/lang/String",
                    "concat",
                    "(Ljava/lang/String;)Ljava/lang/String;",
                    false);
        }
    }

    public Signature reference(BuildData data) {
        Signature ref = left.reference(data);
        if(ref.weakEquals(Signature.fun())) {
            return ref.getGenericReturn(0);
        }
        return ref;
    }

}
