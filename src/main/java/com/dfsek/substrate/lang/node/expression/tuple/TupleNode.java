package com.dfsek.substrate.lang.node.expression.tuple;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.List;

public class TupleNode extends ExpressionNode {
    private final List<ExpressionNode> args;
    private final Position position;

    public TupleNode(List<ExpressionNode> args, Position position) {
        this.args = args;
        this.position = position;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        Signature signature = reference(data);

        Class<?> tuple = data.tupleFactory().generate(signature);

        String tupleName = CompilerUtil.internalName(tuple);
        builder.newInsn(tupleName)
                .dup();

        args.forEach(arg -> {
            arg.simplify().apply(builder, data);
            CompilerUtil.deconstructTuple(arg, data, builder);
        });

        builder.invokeSpecial(tupleName, "<init>", "(" + signature.internalDescriptor() + ")V");
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference(BuildData data) {
        return CompilerUtil.expandArguments(data, args);
    }

    @Override
    public Collection<? extends Node> contents() {
        return args;
    }
}
