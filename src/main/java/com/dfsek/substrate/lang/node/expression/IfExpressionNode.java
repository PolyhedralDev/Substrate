package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import org.objectweb.asm.Label;

import java.util.Arrays;
import java.util.Collection;

public class IfExpressionNode extends ExpressionNode {
    private final ExpressionNode predicate;

    private final ExpressionNode caseTrueNode;
    private final ExpressionNode caseFalseNode;

    public IfExpressionNode(ExpressionNode predicate, ExpressionNode caseTrueNode, ExpressionNode caseFalseNode) {
        this.predicate = predicate;
        this.caseTrueNode = caseTrueNode;
        this.caseFalseNode = caseFalseNode;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkReturnType(predicate, Signature.bool());
        ParserUtil.checkReturnType(caseTrueNode, caseFalseNode.reference().getSimpleReturn());

        Label equal = new Label();
        Label end = new Label();
        predicate.simplify().apply(builder, data);

        builder.ifEQ(equal);

        caseTrueNode.simplify().apply(builder, data);

        builder.goTo(end)
                .label(equal);

        caseFalseNode.simplify().apply(builder, data);

        builder.label(end);
    }

    @Override
    public Position getPosition() {
        return predicate.getPosition();
    }

    @Override
    public Signature reference() {
        return caseTrueNode.reference().getSimpleReturn();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Arrays.asList(predicate, caseTrueNode, caseFalseNode);
    }
}
