package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.IfExpressionNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;

public class IfExpressionRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        ParserUtil.checkType(lexer.consume(), TokenType.IF);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN);
        Unchecked<? extends ExpressionNode> predicate = ExpressionRule.assemble(lexer, data, scope);
        ParserUtil.checkType(lexer.consume(), TokenType.GROUP_END);

        Unchecked<? extends ExpressionNode> caseTrueNode = ExpressionRule.assemble(lexer, data, scope);

        ParserUtil.checkType(lexer.consume(), TokenType.ELSE);

        Unchecked<? extends ExpressionNode> caseFalseNode = ExpressionRule.assemble(lexer, data, scope);

        return IfExpressionNode.of(predicate, caseTrueNode, caseFalseNode);
    }
}
