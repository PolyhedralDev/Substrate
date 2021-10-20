package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.TupleNode;
import com.dfsek.substrate.parser.Parser;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class TupleRule implements Rule {
    private static final TupleRule INSTANCE = new TupleRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token begin = ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN); // Tuples must start with (

        List<ExpressionNode> args = new ArrayList<>();

        int groups = 1;
        while (groups > 0) {
            while (tokenizer.peek().getType() == Token.Type.GROUP_BEGIN) { // sub-groups should just be expanded
                groups++;
                tokenizer.consume();
            }
            args.add(BasicExpressionRule.getInstance().assemble(tokenizer, parser));
            while (tokenizer.peek().getType() == Token.Type.GROUP_END) {
                groups--;
                if(groups < 0) {
                    ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END); // too many close parentheses
                }
                tokenizer.consume();
            }
            if(ParserUtil.checkType(tokenizer.peek(), Token.Type.SEPARATOR, Token.Type.GROUP_END, Token.Type.STATEMENT_END).getType() == Token.Type.SEPARATOR) {
                tokenizer.consume(); // consume separator
            }
        }
        return new TupleNode(args, begin.getPosition());
    }

    public static TupleRule getInstance() {
        return INSTANCE;
    }
}
