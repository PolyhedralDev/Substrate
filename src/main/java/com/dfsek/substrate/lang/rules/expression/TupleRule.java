package com.dfsek.substrate.lang.rules.expression;

import com.dfsek.substrate.lang.Rule;
import com.dfsek.substrate.lang.compiler.build.ParseData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.tuple.TupleNode;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import com.dfsek.substrate.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;

public class TupleRule implements Rule {
    private static final TupleRule INSTANCE = new TupleRule();

    public static TupleRule getInstance() {
        return INSTANCE;
    }

    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, ParseData data, ParserScope scope) throws ParseException {
        Token begin = ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN); // Tuples must start with (

        List<ExpressionNode> args = new ArrayList<>();

        int groups = 1;
        while (groups > 0) {
            while (tokenizer.peek().getType() == Token.Type.GROUP_BEGIN) { // sub-groups should just be expanded
                groups++;
                tokenizer.consume();
            }
            args.add(ExpressionRule.getInstance().assemble(tokenizer, data, scope));
            while (tokenizer.peek().getType() == Token.Type.GROUP_END) {
                groups--;
                if (groups < 0) {
                    ParserUtil.checkType(tokenizer.consume(), Token.Type.STATEMENT_END); // too many close parentheses
                }
                tokenizer.consume();
            }
            if (tokenizer.peek().getType() == Token.Type.SEPARATOR) {
                tokenizer.consume(); // consume separator
            }
        }

        if (args.size() == 1) {
            return args.get(0); // expand out 1-tuples
        }
        return new TupleNode(args, begin.getPosition());
    }
}
