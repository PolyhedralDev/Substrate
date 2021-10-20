package com.dfsek.substrate.lang.rules.expression;

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

public class ExpressionRule implements Rule {
    private final BasicExpressionRule basicExpressionRule = new BasicExpressionRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token test = tokenizer.peek();
        if(test.isConstant() || test.isIdentifier()) { // simple expression
            return basicExpressionRule.assemble(tokenizer, parser);
        }
        // Tuple expression
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_BEGIN); // Tuples must start with (

        List<ExpressionNode> args = new ArrayList<>();
        while (tokenizer.peek().getType() != Token.Type.GROUP_END) {
            args.add(assemble(tokenizer, parser));
        }
        ParserUtil.checkType(tokenizer.consume(), Token.Type.GROUP_END); // Tuples must end with )
        return new TupleNode(args, test.getPosition());
    }
}
