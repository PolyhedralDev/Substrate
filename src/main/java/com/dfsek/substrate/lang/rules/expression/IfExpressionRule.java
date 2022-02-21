package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.IfExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaInvocationNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class IfExpressionRule implements Rule {
    private static final IfExpressionRule INSTANCE = new IfExpressionRule();

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data, ParserScope scope) throws ParseException {
        ParserUtil.checkType(tokenizer.consume(), Token.Type.IF);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);
        ExpressionNode predicate = ExpressionRule.getInstance().assemble(tokenizer, data, scope);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        ExpressionNode caseTrueNode;
        Function<BuildData, ExpressionNode> caseTrue;
        if (tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
            caseTrueNode = BlockRule.getInstance().assemble(tokenizer, data, scope);
            caseTrue = buildData -> new LambdaInvocationNode(new LambdaExpressionNode(caseTrueNode, Collections.emptyList(), caseTrueNode.getPosition(), caseTrueNode.reference()));
        } else {
            caseTrueNode = ExpressionRule.getInstance().assemble(tokenizer, data, scope);
            caseTrue = buildData -> caseTrueNode;
        }

        ExpressionNode caseFalseNode;
        Function<BuildData, ExpressionNode> caseFalse;
        if (tokenizer.peek().getType() == Token.Type.ELSE) {
            tokenizer.consume();
            if (tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
                caseFalseNode = BlockRule.getInstance().assemble(tokenizer, data, scope);
                caseFalse = buildData -> new LambdaInvocationNode(new LambdaExpressionNode(caseFalseNode, Collections.emptyList(), caseFalseNode.getPosition(), caseFalseNode.reference()));
            } else {
                caseFalseNode = ExpressionRule.getInstance().assemble(tokenizer, data, scope);
                caseFalse = buildData -> caseFalseNode;
            }
        } else {
            caseFalseNode = new ExpressionNode() {

                @Override
                public Collection<? extends Node> contents() {
                    return Collections.emptyList();
                }

                @Override
                public Signature reference() {
                    return Signature.empty();
                }

                @Override
                public void apply(MethodBuilder builder, BuildData buildData) throws ParseException {

                }

                @Override
                public Position getPosition() {
                    return predicate.getPosition();
                }
            };
            caseFalse = buildData -> caseFalseNode;
        }

        return new IfExpressionNode(predicate, caseTrue, caseFalse, caseTrueNode, caseFalseNode);
    }

    public static IfExpressionRule getInstance() {
        return INSTANCE;
    }
}
