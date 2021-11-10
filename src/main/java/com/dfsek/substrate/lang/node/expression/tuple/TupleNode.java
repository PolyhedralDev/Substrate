package com.dfsek.substrate.lang.node.expression.tuple;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
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
        Signature signature = reference(data).getGenericReturn(0);

        Class<?> tuple = data.tupleFactory().generate(signature);

        String tupleName = CompilerUtil.internalName(tuple);
        visitor.visitTypeInsn(NEW, tupleName);
        visitor.visitInsn(DUP);

        args.forEach(arg -> {
            arg.apply(visitor, data);
            CompilerUtil.deconstructTuple(arg, data, visitor);
        });

        visitor.visitMethodInsn(INVOKESPECIAL, tupleName, "<init>", "(" + signature.internalDescriptor() + ")V", false);
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.tup().applyGenericReturn(0, CompilerUtil.expandArguments(data, args));
    }
}
