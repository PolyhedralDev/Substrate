package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class IfExpressionNode extends ExpressionNode {
    private final ExpressionNode predicate;
    private final Function<BuildData, ExpressionNode> caseTrue;
    private final Function<BuildData, ExpressionNode> caseFalse;

    private final ExpressionNode caseTrueNode;
    private final ExpressionNode caseFalseNode;

    public IfExpressionNode(ExpressionNode predicate, Function<BuildData, ExpressionNode> caseTrue, Function<BuildData, ExpressionNode> caseFalse, ExpressionNode caseTrueNode, ExpressionNode caseFalseNode) {
        this.predicate = predicate;
        this.caseTrue = caseTrue;
        this.caseFalse = caseFalse;
        this.caseTrueNode = caseTrueNode;
        this.caseFalseNode = caseFalseNode;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkType(predicate, data, Signature.bool());
        ExpressionNode caseFalse = this.caseFalse.apply(data);
        ExpressionNode caseTrue = this.caseTrue.apply(data);
        ParserUtil.checkType(caseTrue, data, caseFalse.reference(data).getSimpleReturn());

        Label equal = new Label();
        Label end = new Label();
        predicate.apply(builder, data);

        builder.ifEQ(equal);

        caseTrue.apply(builder, data);

        builder.goTo(end)
                .label(equal);

        caseFalse.apply(builder, data);

        builder.label(end);
    }

    @Override
    public Position getPosition() {
        return predicate.getPosition();
    }

    @Override
    public Signature reference(BuildData data) {
        return caseTrue.apply(data).reference(data).getSimpleReturn();
    }

    @Override
    public Collection<? extends Node> contents() {
        return Arrays.asList(predicate, caseTrueNode, caseFalseNode);
    }
}
