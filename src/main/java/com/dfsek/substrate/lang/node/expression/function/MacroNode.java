package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;

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
    public List<Either<CompileError, Op>> apply(BuildData data) throws ParseException {
        Signature argSignature = CompilerUtil.expandArguments(args);
        if (!macro.argsMatch(argSignature)) {
            throw new ParseException("Macro expects " + macro.arguments() + ", got " + argSignature, position);
        }

        return macro.prepare()
                .appendAll(macro.invoke(data, argSignature, args));
    }

    @Override
    public Signature reference() {
        return macro.reference(CompilerUtil.expandArguments(args)).getSimpleReturn();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Iterable<? extends Node> contents() {
        return args;
    }
}
