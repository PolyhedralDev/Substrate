package com.dfsek.terrascript.lang.impl.rule.number.binary;

import com.dfsek.terrascript.lang.Rule;
import com.dfsek.terrascript.lang.impl.rule.ExpressionRule;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.Parser;
import com.dfsek.terrascript.parser.ParserUtil;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;
import com.dfsek.terrascript.tokenizer.Tokenizer;

public abstract class BinaryNumberRule implements Rule {
    private final Operation left;

    protected BinaryNumberRule(Operation left) {
        this.left = left;
    }

    @Override
    public Operation assemble(Tokenizer tokenizer, Parser parser) throws ParseException {
        Token op = tokenizer.consume();
        ParserUtil.checkType(op, getOperator());

        Operation right = new ExpressionRule().assemble(tokenizer, parser);


        return newInstance(left, right, op.getPosition());
    }

    public abstract Operation newInstance(Operation left, Operation right, Position position);

    public abstract Token.Type getOperator();
}
