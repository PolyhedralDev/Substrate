package com.dfsek.substrate.lang.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Signature {
    private static final Signature BOOL = new Signature(DataType.BOOL);
    private static final Signature INT = new Signature(DataType.INT);
    private static final Signature DECIMAL = new Signature(DataType.NUM);
    private static final Signature STRING = new Signature(DataType.STR);

    private static final Signature FUN = new Signature(DataType.FUN);

    private static final Signature TUP = new Signature(DataType.TUP);
    private static final Signature VOID = new Signature();
    private final List<DataType> types;

    public Signature(DataType... types) {
        this.types = Arrays.asList(types);
    }

    public Signature(List<DataType> types) {
        this.types = types;
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

    public static Signature empty() {
        return VOID;
    }

    public static Signature fun() {
        return FUN;
    }

    public static Signature tup() {
        return TUP;
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
        if (!(obj instanceof Signature)) return false;
        Signature that = (Signature) obj;

        if (this.size() != that.size()) return false;

        for (int i = 0; i < this.types.size(); i++) {
            if (this.types.get(i) != that.types.get(i)) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < types.size(); i++) {
            builder.append(types.get(i));
            if (i != types.size() - 1) builder.append(',');
        }
        return builder.append(')').toString();
    }

    public Signature and(Signature other) {
        List<DataType> copy = new ArrayList<>(types);
        copy.addAll(other.types);
        return new Signature(copy);
    }

    public Signature and(Signature... more) {
        Signature run = this;
        for (Signature signature : more) {
            run = run.and(signature);
        }
        return run;
    }

    public int frames() {
        if (this.equals(decimal())) return 2;
        return 1;
    }

    public boolean isSimple() {
        return this.types.size() == 1;
    }

    public String classDescriptor() {
        StringBuilder sig = new StringBuilder();
        types.forEach(type -> sig.append(type.descriptorChar()));
        return sig.toString();
    }

    public String internalDescriptor() {
        StringBuilder sig = new StringBuilder();

        types.forEach(type -> sig.append(type.descriptor()));

        return sig.toString();
    }
}
