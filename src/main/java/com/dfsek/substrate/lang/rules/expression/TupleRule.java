package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.tuple.TupleNode;
import com.dfsek.substrate.lexer.Lexer;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import com.dfsek.substrate.parser.scope.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;

public class TupleRule {
    public static Unchecked<? extends ExpressionNode> assemble(Lexer lexer, ParseData data, ParserScope scope) throws ParseException {
        Token begin = ParserUtil.checkType(lexer.consume(), TokenType.GROUP_BEGIN); // Tuples must start with (

        List<Unchecked<? extends ExpressionNode>> args = List.empty();

        int groups = 1;
        while (groups > 0) {
            while (lexer.peek().getType() == TokenType.GROUP_BEGIN) { // sub-groups should just be expanded
                groups++;
                lexer.consume();
            }
            args = args.append(ExpressionRule.assemble(lexer, data, scope));
            while (lexer.peek().getType() == TokenType.GROUP_END) {
                groups--;
                if (groups < 0) {
                    ParserUtil.checkType(lexer.consume(), TokenType.STATEMENT_END); // too many close parentheses
                }
                lexer.consume();
            }
            if (lexer.peek().getType() == TokenType.SEPARATOR) {
                lexer.consume(); // consume separator
            }
        }

        if (args.size() == 1) {
            return args.get(0); // expand out 1-tuples
        }
        return TupleNode.of(args, begin.getPosition());
    }
}
