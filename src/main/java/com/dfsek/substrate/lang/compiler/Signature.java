package com.dfsek.substrate.lang.compiler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Signature {
    private static final Signature BOOL = new Signature(DataType.BOOL);
    private static final Signature INT = new Signature(DataType.INT);
    private static final Signature DECIMAL = new Signature(DataType.NUM);
    private static final Signature STRING = new Signature(DataType.STR);
    private final List<DataType> types;

    public Signature(DataType... types) {
        this.types = Arrays.asList(types);
    }

    public DataType getType(int index) {
        return types.get(index);
    }

    public int size() {
        return types.size();
    }

    public void forEach(Consumer<DataType> action) {
        types.forEach(action);
    }

    @Override
    public int hashCode() {
        int result = 1;

        for (DataType element : types)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Signature)) return false;
        Signature that = (Signature) obj;

        if(this.size() != that.size()) return false;

        for (int i = 0; i < this.types.size(); i++) {
            if(this.types.get(i) != that.types.get(i)) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sig = new StringBuilder();
        types.forEach(type -> sig.append('_').append(type.toString()));
        return sig.toString();
    }

    public static Signature decimal() {
        return DECIMAL;
    }

    public static Signature bool() {
        return BOOL;
    }

    public static Signature integer() {
        return INT;
    }

    public static Signature string() {
        return STRING;
    }
}
