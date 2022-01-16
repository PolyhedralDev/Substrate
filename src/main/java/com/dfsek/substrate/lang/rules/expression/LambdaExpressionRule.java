package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.value.ValueAssignmentNode;
import com.dfsek.substrate.lang.node.expression.value.ValueReferenceNode;
import com.dfsek.substrate.lang.node.expression.function.LambdaExpressionNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.util.pair.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LambdaExpressionRule implements Rule {
    private static final LambdaExpressionRule INSTANCE = new LambdaExpressionRule();

    public static LambdaExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data) throws ParseException {
        Token begin = ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        List<Pair<String, Signature>> types = new ArrayList<>();
        Set<String> args = new HashSet<>();

        while (tokenizer.peek().getType() != Token.Type.GROUP_END) {
            Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
            ParserUtil.checkType(tokenizer.consume(), Token.Type.TYPE);
            types.add(Pair.of(id.getContent(), ParserUtil.parseSignatureNotation(tokenizer)));
            args.add(id.getContent());
            if (ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.GROUP_END).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume();
            }
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);

        Signature returnType;

        if(tokenizer.peek().getType() == Token.Type.TYPE) { // parse type
            ParserUtil.checkType(tokenizer.consume(), Token.Type.TYPE);
            returnType = ParserUtil.parseSignatureNotation(tokenizer);
        } else {
            returnType = Signature.empty(); // void
        }


        ParserUtil.checkType(tokenizer.consume(), Token.Type.ARROW);

        ExpressionNode expression;
        if (tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
            expression = BlockRule.getInstance().assemble(tokenizer, data);
        } else {
            expression = ExpressionRule.getInstance().assemble(tokenizer, data);
        }

        expression.streamScopedContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .filter(node -> args.contains(((ValueReferenceNode) node).getId().getContent()))
                .forEach(node -> ((ValueReferenceNode) node).setLambdaArgument(true));

        Set<String> locals = new HashSet<>();
        expression.streamScopedContents()
                .filter(node -> node instanceof ValueAssignmentNode)
                .forEach(node -> locals.add(((ValueAssignmentNode) node).getId().getContent()));


        expression.streamScopedContents()
                .filter(node -> node instanceof ValueReferenceNode)
                .filter(node -> locals.contains(((ValueReferenceNode) node).getId().getContent()))
                .forEach(node -> ((ValueReferenceNode) node).setLocal(true));


        return new LambdaExpressionNode(expression, types, begin.getPosition(), returnType);
    }
}
