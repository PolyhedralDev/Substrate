package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.IfExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaInvocationNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Collections;

public class IfExpressionRule implements Rule {
    private static final IfExpressionRule INSTANCE = new IfExpressionRule();

    public static IfExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        ParserUtil.checkType(lexer.consume(), TokenType.IF);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);
        ExpressionNode predicate = ExpressionRule.getInstance().assemble(lexer, data, scope);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);

        ExpressionNode caseTrueNode;
        if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) {
            ExpressionNode internal = BlockRule.getInstance().assemble(lexer, data, scope);
            caseTrueNode = new LambdaInvocationNode(new LambdaExpressionNode(internal, List.empty(), internal.getPosition(), internal.reference(), HashSet.empty()));
        } else {
            caseTrueNode = ExpressionRule.getInstance().assemble(lexer, data, scope);
        }

        ExpressionNode caseFalseNode;
        if (lexer.peek().getType() == TokenType.ELSE) {
            lexer.consume();
            if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) {
                ExpressionNode internal = BlockRule.getInstance().assemble(lexer, data, scope);
                caseFalseNode = new LambdaInvocationNode(new LambdaExpressionNode(internal, List.empty(), internal.getPosition(), internal.reference(), HashSet.empty()));
            } else {
                caseFalseNode = ExpressionRule.getInstance().assemble(lexer, data, scope);
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
                public List<Either<CompileError, Op>> apply(BuildData buildData) throws ParseException {
                    return List.empty();
                }

                @Override
                public Position getPosition() {
                    return predicate.getPosition();
                }
            };
        }

        return new IfExpressionNode(predicate, caseTrueNode, caseFalseNode);
    }
}
