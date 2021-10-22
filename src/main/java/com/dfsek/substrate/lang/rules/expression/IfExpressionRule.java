package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.IfExpressionNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;
import org.objectweb.asm.MethodVisitor;

public class IfExpressionRule implements Rule {
    @Override
    public Node assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.IF);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);
        ExpressionNode predicate = ExpressionRule.getInstance().assemble(tokenizer, parser);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        ExpressionNode caseTrue = ExpressionRule.getInstance().assemble(tokenizer, parser);

        ExpressionNode caseFalse;
        if(tokenizer.peek().getType() == Token.Type.ELSE) {
            caseFalse = ExpressionRule.getInstance().assemble(tokenizer, parser);
        } else {
            caseFalse = new ExpressionNode() {
                @Override
                public Signature returnType(BuildData data) {
                    return Signature.empty();
                }

                @Override
                public void apply(MethodVisitor visitor, BuildData data) throws ParseException {

                }

                @Override
                public Position getPosition() {
                    return caseTrue.getPosition();
                }
            };
        }

        return new IfExpressionNode(predicate, caseTrue, caseFalse);
    }
}
