package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.*;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaInvocationNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.Collection;
import java.util.Collections;

public class IfExpressionRule implements Rule {
    private static final IfExpressionRule INSTANCE = new IfExpressionRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.IF);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);
        ExpressionNode predicate = ExpressionRule.getInstance().assemble(tokenizer, data);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        ExpressionNode caseTrue;

        if(tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
            ExpressionNode block = BlockRule.getInstance().assemble(tokenizer, data);
            LambdaExpressionNode lambda = new LambdaExpressionNode(block, Collections.emptyList(), block.getPosition());
            caseTrue = new LambdaInvocationNode(lambda);
        } else {
            caseTrue = ExpressionRule.getInstance().assemble(tokenizer, data);
        }

        ExpressionNode caseFalse;
        if(tokenizer.peek().getType() == Token.Type.ELSE) {
            tokenizer.consume();
            if(tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
                ExpressionNode block = BlockRule.getInstance().assemble(tokenizer, data);
                LambdaExpressionNode lambda = new LambdaExpressionNode(block, Collections.emptyList(), block.getPosition());
                caseFalse = new LambdaInvocationNode(lambda);
            } else {
                caseFalse = ExpressionRule.getInstance().assemble(tokenizer, data);
            }
        } else {
            caseFalse = new ExpressionNode() {

                @Override
                public Collection<? extends Node> contents() {
                    return Collections.emptyList();
                }

                @Override
                public Signature reference(BuildData buildData) {
                    return Signature.empty();
                }

                @Override
                public void apply(MethodBuilder builder, BuildData buildData) throws ParseException {

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
