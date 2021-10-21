package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.cast.IntToNumCastNode;
import com.dfsek.substrate.lang.node.expression.cast.NumToIntCastNode;
import com.dfsek.substrate.lang.node.expression.cast.ToStringNode;
import com.dfsek.substrate.lang.node.expression.cast.TypeCastNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

public class CastRule implements Rule {
    private static final CastRule INSTANCE = new CastRule();

    @Override
    public TypeCastNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token type = ParserUtil.checkType(tokenizer.consume(), Token.Type.STRING_TYPE, Token.Type.INT_TYPE, Token.Type.NUM_TYPE);
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN);

        ExpressionNode expressionNode = ExpressionRule.getInstance().assemble(tokenizer, parser);

        TypeCastNode node;
        if (type.getType() == Token.Type.INT_TYPE) {
            node = new NumToIntCastNode(type, expressionNode);
        } else if (type.getType() == Token.Type.STRING_TYPE) {
            node = new ToStringNode(type, expressionNode);
        } else if(type.getType() == Token.Type.NUM_TYPE) {
            node = new IntToNumCastNode(type, expressionNode);
        } else {
            throw new ParseException("Invalid type: " + type.getType(), type.getPosition()); // Should never happen
        }
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END);
        return node;
    }

    public static CastRule getInstance() {
        return INSTANCE;
    }
}
