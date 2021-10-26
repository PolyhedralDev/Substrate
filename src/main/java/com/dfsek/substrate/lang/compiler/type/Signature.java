package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.util.pair.Pair;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;



public class Signature implements Opcodes {
    private static final Signature BOOL = new Signature(DataType.BOOL);
    private static final Signature INT = new Signature(DataType.INT);
    private static final Signature DECIMAL = new Signature(DataType.NUM);
    private static final Signature STRING = new Signature(DataType.STR);

    private static final Signature FUN = new Signature(DataType.FUN);

    private static final Signature TUP = new Signature(DataType.TUP);
    private static final Signature VOID = new Signature();
    private final List<DataType> types;
    private final List<Pair<Signature, Signature>> generic;

    public Signature(DataType type) {
        this.types = Collections.singletonList(type);
        this.generic = Collections.singletonList(Pair.of(Signature.empty(), Signature.empty()));
    }

    private Signature() {
        this.types = Collections.emptyList();
        this.generic = Collections.emptyList();
    }

    private Signature(List<DataType> types, List<Pair<Signature, Signature>> generic) {
        this.types = types;
        this.generic = generic;
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

    public Signature getGenericReturn(int index) {
        return generic.get(index).getRight();
    }

    public Signature getGenericArguments(int index) {
        return generic.get(index).getLeft();
    }

    public Signature applyGenericReturn(int index, Signature generic) {
        List<DataType> otherTypes = new ArrayList<>(types);
        List<Pair<Signature, Signature>> otherGeneric = new ArrayList<>(this.generic);
        otherGeneric.set(index, Pair.of(otherGeneric.get(index).getLeft(), generic));
        return new Signature(otherTypes, otherGeneric);
    }

    public Signature applyGenericArgument(int index, Signature generic) {
        List<DataType> otherTypes = new ArrayList<>(types);
        List<Pair<Signature, Signature>> otherGeneric = new ArrayList<>(this.generic);
        otherGeneric.set(index, Pair.of(generic, otherGeneric.get(index).getRight()));
        return new Signature(otherTypes, otherGeneric);
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
        List<DataType> otherTypes = new ArrayList<>(types);
        otherTypes.addAll(other.types);
        List<Pair<Signature, Signature>> otherGeneric = new ArrayList<>(generic);
        otherGeneric.addAll(other.generic);
        return new Signature(otherTypes, otherGeneric);
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
