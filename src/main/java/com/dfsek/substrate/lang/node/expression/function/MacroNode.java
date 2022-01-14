package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.List;

public class MacroNode extends ExpressionNode {
    private final Macro macro;
    private final Position position;
    private final List<ExpressionNode> args;

    public MacroNode(Macro macro, Position position, List<ExpressionNode> args) {
        this.macro = macro;
        this.position = position;
        this.args = args;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        Signature argSignature = CompilerUtil.expandArguments(data, args);
        if(!macro.argsMatch(argSignature)) {
            throw new ParseException("Macro expects " + macro.arguments() + ", got " + argSignature, position);
        }

        macro.prepare(builder);

        args.forEach(arg -> arg.apply(builder, data));

        macro.invoke(builder, data, argSignature);
    }

    @Override
    public Signature reference(BuildData data) {
        return macro.reference(data).getSimpleReturn();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Collection<? extends Node> contents() {
        return args;
    }
}
