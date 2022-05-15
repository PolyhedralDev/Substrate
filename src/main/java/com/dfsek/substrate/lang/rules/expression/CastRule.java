package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.cast.IntToNumCastNode;
import com.dfsek.substrate.lang.node.expression.cast.NumToIntCastNode;
import com.dfsek.substrate.lang.node.expression.cast.ToStringNode;
import com.dfsek.substrate.lang.node.expression.cast.TypeCastNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

public class CastRule {
    public static Unchecked<? extends TypeCastNode<?, ?>> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token type = ParserUtil.checkType(lexer.consume(), TokenType.STRING_TYPE, TokenType.INT_TYPE, TokenType.NUM_TYPE);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);

        Unchecked<?> expressionNode = ExpressionRule.assemble(lexer, data, scope);

        Unchecked<? extends TypeCastNode<?, ?>> node;
        if (type.getType() == TokenType.INT_TYPE) {
            node = NumToIntCastNode.of(type, expressionNode);
        } else if (type.getType() == TokenType.STRING_TYPE) {
            node = ToStringNode.of(type, expressionNode);
        } else if (type.getType() == TokenType.NUM_TYPE) {
            node = IntToNumCastNode.of(type, expressionNode);
        } else {
            throw new ParseException("Invalid type: " + type.getType(), type.getPosition()); // Should never happen
        }
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);
        return node;
    }
}
