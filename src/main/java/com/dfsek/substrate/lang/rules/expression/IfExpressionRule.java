package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.*;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;
import org.objectweb.asm.MethodVisitor;

import java.util.Collections;

public class IfExpressionRule implements Rule {
    private static final IfExpressionRule INSTANCE = new IfExpressionRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.IF);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);
        ExpressionNode predicate = ExpressionRule.getInstance().assemble(tokenizer, parser);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        ExpressionNode caseTrue;

        if(tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
            ExpressionNode block = BlockRule.getInstance().assemble(tokenizer, parser);
            LambdaExpressionNode lambda = new LambdaExpressionNode(block, Collections.emptyList(), block.getPosition());
            caseTrue = new LambdaInvocationNode(lambda);
        } else {
            caseTrue = ExpressionRule.getInstance().assemble(tokenizer, parser);
        }

        ExpressionNode caseFalse;
        if(tokenizer.peek().getType() == Token.Type.ELSE) {
            tokenizer.consume();
            if(tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
                ExpressionNode block = BlockRule.getInstance().assemble(tokenizer, parser);
                LambdaExpressionNode lambda = new LambdaExpressionNode(block, Collections.emptyList(), block.getPosition());
                caseFalse = new LambdaInvocationNode(lambda);
            } else {
                caseFalse = ExpressionRule.getInstance().assemble(tokenizer, parser);
            }
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

    public static IfExpressionRule getInstance() {
        return INSTANCE;
    }
}
