package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.Label;

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
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        ParserUtil.checkType(predicate, data, Signature.bool());
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
        return caseTrue.reference(data).getSimpleReturn();
    }
}
