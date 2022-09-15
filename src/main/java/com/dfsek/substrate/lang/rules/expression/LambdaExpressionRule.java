package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.PrimitiveValue;
import com.dfsek.substrate.lang.compiler.value.ThisReferenceValue;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.Tuple2;
import io.vavr.collection.List;

import java.util.HashSet;
import java.util.Set;

public class LambdaExpressionRule {
    private LambdaExpressionRule() {

    }
    public static Unchecked<LambdaExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope, String variableName) throws ParseException {
        ParserScope lambda = scope;
        Token begin = ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);

        List<Tuple2<String, Signature>> types = List.empty();
        Set<String> args = new HashSet<>();

        Signature argSig = Signature.empty();

        int i = 2; // 0 = this, 1 = Environment
        while (lexer.peek().getType() != TokenType.GROUP_END) {
            Token id = ParserUtil.checkType(lexer.consume(), TokenType.IDENTIFIER);
            ParserUtil.checkType(lexer.consume(), TokenType.TYPE);
            String argName = id.getContent();
            Signature argSignature = ParserUtil.parseSignatureNotation(lexer);
            argSig = argSig.and(argSignature);
            types = types.append(new Tuple2<>(argName, argSignature));

            lambda = lambda.register(argName, new PrimitiveValue(argSignature, argName, i, argSignature.frames()));
            i += argSignature.frames();

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
            lambda = lambda.register(variableName, new ThisReferenceValue(Signature.fun().applyGenericArgument(0, argSig).applyGenericReturn(0, returnType)));
        }

        ParserUtil.checkType(lexer.consume(), TokenType.ARROW);

        Unchecked<? extends ExpressionNode> uncheckedExpression = ExpressionRule.assemble(lexer, data, lambda);


        ExpressionNode expression = uncheckedExpression.get(returnType);

        Set<String> locals = new HashSet<>();
        expression.streamContents()
                .filter(ValueAssignmentNode.class::isInstance)
                .forEach(node -> locals.add(((ValueAssignmentNode) node).getId().getContent()));

        expression.streamContents()
                .filter(ValueReferenceNode.class::isInstance)
                .filter(node -> locals.contains(((ValueReferenceNode) node).getId().getContent()))
                .forEach(node -> ((ValueReferenceNode) node).setLocal(true));

        return LambdaExpressionNode.of(
                uncheckedExpression,
                types,
                begin.getPosition(),
                returnType,
                expression.streamContents()
                        .filter(ValueReferenceNode.class::isInstance)
                        .filter(node -> args.contains(((ValueReferenceNode) node).getId().getContent()))
                        .map(node -> {
                            ((ValueReferenceNode) node).setLambdaArgument(true);
                            return ((ValueReferenceNode) node).getId().getContent();
                        })
                        .toSet()
        );
    }

    public static Unchecked<LambdaExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return assemble(lexer, data, scope, null);
    }
}
