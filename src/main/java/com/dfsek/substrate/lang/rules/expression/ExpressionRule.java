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
    private final TupleRule tupleRule = new TupleRule();
    @Override
    public ExpressionNode assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token test = tokenizer.peek();
        if(test.isConstant() || test.isIdentifier()) { // simple expression
            return basicExpressionRule.assemble(tokenizer, parser);
        }
        // Tuple expression
        return tupleRule.assemble(tokenizer, parser);
    }
}
