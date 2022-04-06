package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.HashSet;
import java.util.Set;

public class LambdaExpressionRule {
    public static ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope, String variableName) throws ParseException {
        ParserScope lambda = scope.sub();
        Token begin = ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);

        List<Tuple2<String, Signature>> types = List.empty();
        Set<String> args = new HashSet<>();

        Signature argSig = Signature.empty();

        while (lexer.peek().getType() != TokenType.GROUP_END) {
            Token id = ParserUtil.checkType(lexer.consume(), TokenType.IDENTIFIER);
            ParserUtil.checkType(lexer.consume(), TokenType.TYPE);
            String argName = id.getContent();
            Signature argSignature = ParserUtil.parseSignatureNotation(lexer);
            argSig = argSig.and(argSignature);
            types = types.append(new Tuple2<>(argName, argSignature));

            lambda.register(argName, argSignature);

            args.add(id.getContent());
            if (ParserUtil.checkType(lexer.peek(), TokenType.SEPARATOR, TokenType.GROUP_END).getType() == TokenType.SEPARATOR) {
                lexer.consume();
            }
        }

        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);

        Signature returnType;

        if (lexer.peek().getType() == TokenType.TYPE) { // parse type
            ParserUtil.checkType(lexer.consume(), TokenType.TYPE);
            returnType = ParserUtil.parseSignatureNotation(lexer);
        } else {
            returnType = Signature.empty(); // void
        }

        if (variableName != null) {
            lambda.register(variableName, Signature.fun().applyGenericArgument(0, argSig).applyGenericReturn(0, returnType));
        }

        ParserUtil.checkType(lexer.consume(), TokenType.ARROW);

        ExpressionNode expression;
        if (lexer.peek().getType() == TokenType.BLOCK_BEGIN) {
            expression = BlockRule.assemble(lexer, data, lambda);
        } else {
            expression = ExpressionRule.assemble(lexer, data, lambda);
        }

        Set<String> locals = new HashSet<>();
        expression.streamContents()
                .filter(node -> node instanceof ValueAssignmentNode)
                .forEach(node -> locals.add(((ValueAssignmentNode) node).getId().getContent()));

        expression.streamContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .filter(node -> locals.contains(((ValueReferenceNode) node).getId().getContent()))
                .forEach(node -> ((ValueReferenceNode) node).setLocal(true));

        return new LambdaExpressionNode(
                expression,
                types,
                begin.getPosition(),
                returnType,
                expression.streamContents()
                        .filter(node -> node instanceof ValueReferenceNode)
                        .filter(node -> args.contains(((ValueReferenceNode) node).getId().getContent()))
                        .map(node -> {
                            ((ValueReferenceNode) node).setLambdaArgument(true);
                            return ((ValueReferenceNode) node).getId().getContent();
                        })
                        .toSet()
        );
    }

    public static ExpressionNode assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return assemble(lexer, data, scope, null);
    }
}
