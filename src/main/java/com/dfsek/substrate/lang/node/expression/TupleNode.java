package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.util.ReflectionUtil;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class TupleNode extends ExpressionNode {
    private final List<ExpressionNode> args;
    private final Position position;

    public TupleNode(List<ExpressionNode> args, Position position) {
        this.args = args;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Signature signature = returnType(data);

        Class<?> tuple = data.tupleFactory().generate(signature);

        String tupleName = ReflectionUtil.internalName(tuple);
        visitor.visitTypeInsn(NEW, tupleName);
        visitor.visitInsn(DUP);

        args.forEach(arg -> arg.apply(visitor, data));

        visitor.visitMethodInsn(INVOKESPECIAL, tupleName, "<init>", "(" + signature.internalDescriptor() + ")V", false);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature returnType(BuildData data) {
        Signature signature;
        if(args.isEmpty()) {
            signature = Signature.empty();
        } else if(args.size() == 1) {
            signature = args.get(0).returnType(data);
        } else {
            signature = args.get(0).returnType(data);
            for (int i = 1; i < args.size(); i++) {
                signature = signature.and(args.get(i).returnType(data));
            }
        }
        return signature;
    }
}
