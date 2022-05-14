package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.control.Either;

import java.util.Arrays;

public class Unchecked<T extends Typed> {
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
}
