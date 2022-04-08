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
        return Match(ParserUtil.checkType(lexer.peek(),
                TokenType.IDENTIFIER,
                TokenType.STRING,
                TokenType.BOOLEAN,
                TokenType.NUMBER,
                TokenType.INT))
                .of(Case($(token -> token.getType() == TokenType.STRING),
                                token -> new StringNode(lexer.consume().getContent(), token.getPosition())),
                        Case($(token -> token.getType() == TokenType.BOOLEAN), token ->
                                Try(() -> (ExpressionNode) new BooleanNode(Boolean.parseBoolean(lexer.consume().getContent()), token.getPosition()))
                                        .getOrElse(new ErrorNode(token.getPosition(), "Malformed boolean literal", Signature.bool()))),
                        Case($(token -> token.getType() == TokenType.NUMBER), token ->
                                Try(() -> (ExpressionNode) new DecimalNode(Double.parseDouble(lexer.consume().getContent()), token.getPosition()))
                                        .getOrElse(new ErrorNode(token.getPosition(), "Malformed decimal literal", Signature.decimal()))),
                        Case($(token -> token.getType() == TokenType.INT), token ->
                                Try(() -> (ExpressionNode) new IntegerNode(Integer.parseInt(lexer.consume().getContent()), token.getPosition()))
                                        .getOrElse(new ErrorNode(token.getPosition(), "Malformed integer literal", Signature.integer()))),
                        Case($(token -> lexer.peek(1).getType() == TokenType.ASSIGNMENT),
                                () -> ValueAssignmentRule.assemble(lexer, data, scope)),
                        Case($(),
                                token -> new ValueReferenceNode(
                                        ParserUtil.checkType(token, TokenType.IDENTIFIER),
                                        Try(() -> scope.get(lexer.consume().getContent()))
                                                .getOrElseThrow(() -> new ParseException("No such value: " + token.getContent(), token.getPosition()))
                                ))
                );
    }
}
