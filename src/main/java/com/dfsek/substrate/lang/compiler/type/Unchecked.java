package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import io.vavr.control.Either;

import java.util.Arrays;
import java.util.function.Function;

public final class Unchecked<T extends Typed> implements Typed{
    private Unchecked(T value) {
        this.value = value;
    }

    public static <T1 extends Typed> Unchecked<T1> of(T1 value) {
        return new Unchecked<>(value);
    }

    private final T value;

    public Either<String, T> get(Signature... expected) {
        Signature ref = value.reference();
        for (Signature type : expected) if (ref.equals(type)) return Either.right(value);
        return Either.left("Expected type(s) " + Arrays.toString(expected) + " but found " + ref);
    }

    public static <T extends ExpressionNode> Folded<T> fold(Unchecked<T> unchecked) {
        return new Folded<>(unchecked);
    }

    @Override
    public Signature reference() {
        return value.reference();
    }

    public static final class Folded<T extends ExpressionNode> implements Typed {
        private final Unchecked<T> value;

        private Folded(Unchecked<T> value) {
            this.value = value;
        }

        public ExpressionNode get(Signature... signatures) {
            return value.get(signatures)
                    .fold(
                            err -> new ErrorNode(value.value.getPosition(), err),
                            Function.identity()
                    );
        }

        @Override
        public Signature reference() {
            return value.value.reference();
        }
    }
}
