package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class BooleanAndNode extends BooleanOperationNode{
    public BooleanAndNode(ExpressionNode left, ExpressionNode right, Token op) {
        super(left, right, op);
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {


        left.apply(visitor, data);

    }
}
