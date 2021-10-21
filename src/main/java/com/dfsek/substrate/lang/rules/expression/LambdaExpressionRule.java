package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.LambdaExpressionNode;
import com.dfsek.substrate.lang.rules.BlockRule;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;
import com.dfsek.substrate.util.pair.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

public class LambdaExpressionRule implements Rule {
    private static final LambdaExpressionRule INSTANCE = new LambdaExpressionRule();

    public static LambdaExpressionRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token begin = ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        List<ImmutablePair<String, DataType>> types = new ArrayList<>();

        while (tokenizer.peek().getType() != Token.Type.GROUP_END) {
            Token id = ParserUtil.checkType(tokenizer.consume(), Token.Type.IDENTIFIER);
            ParserUtil.checkType(tokenizer.consume(), Token.Type.TYPE);
            DataType type = DataType
                    .fromToken(
                            ParserUtil
                                    .checkType(tokenizer.consume(), Token.Type.STRING_TYPE, Token.Type.BOOL_TYPE, Token.Type.INT_TYPE, Token.Type.NUM_TYPE, Token.Type.FUN_TYPE));
            types.add(ImmutablePair.of(id.getContent(), type));
            if (ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.GROUP_END).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume();
            }
        }

        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.ARROW);

        ExpressionNode expression;
        if (tokenizer.peek().getType() == Token.Type.BLOCK_BEGIN) {
            expression = BlockRule.getInstance().assemble(tokenizer, parser);
        } else {
            expression = ExpressionRule.getInstance().assemble(tokenizer, parser);
        }


        return new LambdaExpressionNode(expression, types, begin.getPosition());
    }
}
