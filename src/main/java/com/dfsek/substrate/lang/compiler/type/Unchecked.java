package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.read.Position;

import java.util.Arrays;

public final class Unchecked<T extends ExpressionNode> implements Typed {
    private Unchecked(T value) {
        this.value = value;
    }

    public static <T1 extends ExpressionNode> Unchecked<T1> of(T1 value) {
        return new Unchecked<>(value);
    }

    private final T value;

    public ExpressionNode get(Signature... signatures) {
        ExpressionNode simplified = value.simplify();
        System.out.println("NODE:::" + value);
        Signature ref = simplified.reference();
        if(!(simplified instanceof ErrorNode)) {
            for (Signature type : signatures) if (ref.equals(type)) return simplified;
        }

        String err = "";

        if(value instanceof ErrorNode errorNode) {
            System.out.println("ERROR: " + errorNode.getMessage());
            err = " due to error: " + errorNode.getMessage();
        }

        return new ErrorNode(simplified.getPosition(), "Expected type(s) " + Arrays.toString(signatures) + " but found " + ref + err, ref);
    }

    public ExpressionNode weak(Signature... signatures) {
        ExpressionNode simplified = value.simplify();
        Signature ref = simplified.reference();
        for (Signature type : signatures) if (ref.weakEquals(type)) return simplified;
        return new ErrorNode(simplified.getPosition(), "Expected type(s) " + Arrays.toString(signatures) + " but found " + ref, ref);
    }

    public T unchecked() {
        return value;
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    public Position getPosition() {
        return value.getPosition();
    }
}
