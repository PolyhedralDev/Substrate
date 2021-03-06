package com.dfsek.terrascript.lang.impl.rule.number.binary;

import com.dfsek.terrascript.lang.impl.operations.number.binary.AdditionOperation;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.tokenizer.Position;
import com.dfsek.terrascript.tokenizer.Token;

public class AdditionRule extends BinaryNumberRule{
    protected AdditionRule(Operation left) {
        super(left);
    }

    @Override
    public Operation newInstance(Operation left, Operation right, Position position) {
        return new AdditionOperation(left, right, position);
    }

    @Override
    public Token.Type getOperator() {
        return Token.Type.ADDITION_OPERATOR;
    }
}
