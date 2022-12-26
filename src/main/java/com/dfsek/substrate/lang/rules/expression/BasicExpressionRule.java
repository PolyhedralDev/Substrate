package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lang.node.expression.constant.StringNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

import java.util.function.Function;

import static io.vavr.API.*;

public class BasicExpressionRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return ParserUtil.checkTypeFunctional(lexer.peek(),
                        TokenType.IDENTIFIER,
                        TokenType.STRING,
                        TokenType.BOOLEAN,
                        TokenType.NUMBER,
                        TokenType.INT)
                .fold(ErrorNode::of, token -> Match(token.getType())
                        .of(
                                Case($(TokenType.STRING), () ->
                                        StringNode.of(
                                                lexer.consume().getContent(),
                                                token.getPosition()
                                        )),
                                Case($(TokenType.BOOLEAN), () ->
                                        BooleanNode.of(
                                                lexer.consume().getContent(),
                                                token.getPosition()
                                        )),
                                Case($(TokenType.NUMBER), () ->
                                        DecimalNode.of(
                                                lexer.consume().getContent(),
                                                token.getPosition()
                                        )),
                                Case($(TokenType.INT), () ->
                                        IntegerNode.of(
                                                lexer.consume().getContent(),
                                                token.getPosition()
                                        )),
                                Case($(),
                                        () -> ParserUtil.checkTypeFunctional(token, TokenType.IDENTIFIER)
                                                .fold(ErrorNode::of,
                                                        id -> scope.get(lexer.consume().getContent())
                                                                .map(value -> ValueReferenceNode.of(
                                                                        id,
                                                                        value.reference()
                                                                )).fold(
                                                                        () -> ErrorNode.of(token.getPosition(), "No such value: " + token.getContent()),
                                                                        Function.identity()
                                                                )
                                                ))
                        ));
    }
}
