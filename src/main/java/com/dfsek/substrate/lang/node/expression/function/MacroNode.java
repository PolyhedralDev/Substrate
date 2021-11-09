package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.api.Macro;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

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
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Signature argSignature = CompilerUtil.expandArguments(data, args);
        if(!macro.argsMatch(argSignature)) {
            throw new ParseException("Macro expects " + macro.arguments() + ", got " + argSignature, position);
        }

        macro.prepare(visitor);

        args.forEach(arg -> arg.apply(visitor, data));

        macro.invoke(visitor, data, argSignature);
    }

    @Override
    public Signature reference(BuildData data) {
        return macro.reference(data).getSimpleReturn();
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
