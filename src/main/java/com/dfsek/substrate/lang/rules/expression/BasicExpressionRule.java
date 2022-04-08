package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lang.rules.value.ValueAssignmentRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

import static io.vavr.API.*;

public class BasicExpressionRule {
    public static <P extends Record, R extends Record> ExpressionNode assemble(Lexer lexer, ParseData<P, R> data, ParserScope scope) throws ParseException {
        return ParserUtil.checkTypeFunctional(lexer.peek(),
                        TokenType.IDENTIFIER,
                        TokenType.STRING,
                        TokenType.BOOLEAN,
                        TokenType.NUMBER,
                        TokenType.INT)
                .fold(ErrorNode::new, token -> Match(token.getType())
                        .of(
                                Case($(TokenType.STRING),
                                        () -> new StringNode(lexer.consume().getContent(), token.getPosition())),
                                Case($(TokenType.BOOLEAN), () ->
                                        Try(() -> (ExpressionNode) new BooleanNode(
                                                Boolean.parseBoolean(lexer.consume().getContent()),
                                                token.getPosition()
                                        )).getOrElse(new ErrorNode(
                                                token.getPosition(),
                                                "Malformed boolean literal",
                                                Signature.bool()
                                        ))),
                                Case($(TokenType.NUMBER), () ->
                                        Try(() -> (ExpressionNode) new DecimalNode(
                                                Double.parseDouble(lexer.consume().getContent()),
                                                token.getPosition()
                                        )).getOrElse(new ErrorNode(
                                                token.getPosition(),
                                                "Malformed decimal literal",
                                                Signature.decimal()
                                        ))),
                                Case($(TokenType.INT), () ->
                                        Try(() -> (ExpressionNode) new IntegerNode(
                                                Integer.parseInt(lexer.consume().getContent()),
                                                token.getPosition()
                                        )).getOrElse(new ErrorNode(
                                                token.getPosition(),
                                                "Malformed integer literal",
                                                Signature.integer()
                                        ))),
                                Case($(t -> lexer.peek(1).getType() == TokenType.ASSIGNMENT),
                                        () -> ValueAssignmentRule.assemble(lexer, data, scope)),
                                Case($(), () -> ParserUtil.checkTypeFunctional(token, TokenType.IDENTIFIER)
                                        .fold(ErrorNode::new, id ->
                                                Try(() -> (ExpressionNode) new ValueReferenceNode(
                                                        id,
                                                        scope.get(lexer.consume().getContent())
                                                )).getOrElse(() -> new ErrorNode(token.getPosition(), "No such value: " + token.getContent()))
                                        ))
                        ));
    }
}
