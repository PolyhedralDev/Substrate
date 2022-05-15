package com.dfsek.substrate.lang.rules;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.ReturnNode;
import com.dfsek.substrate.lang.rules.expression.ExpressionRule;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

public class ReturnRule {
    public static Unchecked<ReturnNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        return assemble(lexer, data, scope, null);
    }

    public static Unchecked<ReturnNode> assemble(Lexer lexer, ParseData data, ParserScope scope, Signature record) throws ParseException {
        Token r = ParserUtil.checkType(lexer.consume(), TokenType.RETURN);

        Unchecked<? extends ExpressionNode> expressionNode = ExpressionRule.assemble(lexer, data, scope);
        Unchecked<ReturnNode> node = ReturnNode.of(r.getPosition(), expressionNode, record);

        ParserUtil.checkType(lexer.consume(), TokenType.STATEMENT_END);
        return node;
    }
}
