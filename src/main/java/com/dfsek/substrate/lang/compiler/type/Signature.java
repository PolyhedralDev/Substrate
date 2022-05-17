package com.dfsek.substrate.lang.compiler.type;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

import static io.vavr.API.*;


public class Signature implements Opcodes {
    private static final Signature VOID = new Signature();
    private static final Signature BOOL = new Signature(DataType.BOOL);
    private static final Signature INT = new Signature(DataType.INT);
    private static final Signature DECIMAL = new Signature(DataType.NUM);
    private static final Signature STRING = new Signature(DataType.STR);
    private static final Signature FUN = new Signature(DataType.FUN);
    private static final Signature IO = new Signature(DataType.IO);

    private static final Signature LIST = new Signature(DataType.LIST);
    private static final Signature ANY = new Signature(DataType.ANY);
    private final List<DataType> types;
    private final List<Tuple2<Signature, Signature>> generic;

    public Signature(DataType type) {
        this.types = List(type);
        this.generic = List(new Tuple2<>(Signature.empty(), Signature.empty()));
    }

    private Signature() {
        this.types = List();
        this.generic = List();
    }

    private Signature(List<DataType> types, List<Tuple2<Signature, Signature>> generic) {
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

    public static Signature io() {
        return IO;
    }

    public static Signature any() {
        return ANY;
    }

    public static Signature list() {
        return LIST;
    }

    public DataType getType(int index) {
        return types.get(index);
    }

    public int size() {
        return types.size();
    }

    @Override
    public int hashCode() {
        int result = 1;

        for (DataType element : types)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        for (Tuple2<Signature, Signature> pair : generic) {
            result = 31 * result + (pair == null ? 0 : pair.hashCode());
        }

        return result;
    }

    public Signature getGenericReturn(int index) {
        Signature ret = generic.get(index)._2;
        return ret == null ? empty() : ret;
    }

    public Signature getGenericArguments(int index) {
        Signature arg = generic.get(index)._1;
        return arg == null ? empty() : arg;
    }

    public Signature get(int index) {
        return new Signature(types.get(index)).applyGenericReturn(0, getGenericReturn(index)).applyGenericArgument(0, getGenericArguments(index));
    }

    public Signature applyGenericReturn(int index, Signature generic) {
        Objects.requireNonNull(generic);
        Signature left = this.generic.get(index)._1;
        List<Tuple2<Signature, Signature>> other = this.generic.update(index, new Tuple2<>(left == null ? empty() : left, generic));
        return new Signature(types, other);
    }

    public Signature applyGenericArgument(int index, Signature generic) {
        Objects.requireNonNull(generic);
        Signature right = this.generic.get(index)._2;
        List<Tuple2<Signature, Signature>> other = this.generic.update(index, new Tuple2<>(generic, right == null ? empty() : right));
        return new Signature(types, other);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Signature that)) return false;

        if (this.size() != that.size()) return false;

        for (int i = 0; i < this.types.size(); i++) {
            if (this.types.get(i) != that.types.get(i)) return false;
        }

        for (int i = 0; i < generic.size(); i++) {
            if (!this.generic.get(i).equals(that.generic.get(i))) return false;
        }

        return true;
    }

    public boolean weakEquals(Signature that) {
        if (this.size() != that.size()) return false;

        for (int i = 0; i < this.types.size(); i++) {
            DataType thisType = this.types.get(i);
            DataType thatType = that.types.get(i);

            if (thisType == DataType.ANY || thatType == DataType.ANY) {
                return true;
            }
            if (thisType != thatType) {
                return false;
            }
        }

        return true;
    }

    public Signature getSimpleReturn() {
        if (this.equals(empty())) return empty();
        if (!isSimple()) {
            throw new IllegalStateException("Cannot get simple return value of non-simple Signature " + this);
        }
        if (this.weakEquals(fun())) return getGenericReturn(0);
        if (!getGenericReturn(0).equals(empty())) return getGenericReturn(0);
        return this;
    }

    @Override
    public String toString() {
        if (this.equals(empty())) return "()";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            builder.append(types.get(i));
            if (!getGenericArguments(i).equals(empty()) || !getGenericReturn(i).equals(empty())) {
                builder.append('<');
                builder.append(getGenericArguments(i).toString());
                builder.append("->");
                builder.append(getGenericReturn(i).toString());
                builder.append('>');
            }
            if (i != types.size() - 1) builder.append(',');
        }
        return builder.toString();
    }

    public Signature and(Signature other) {
        return new Signature(this.types.appendAll(other.types), this.generic.appendAll(other.generic));
    }

    public int frames() {
        int frames = 0;
        for (DataType type : types) {
            if (type == DataType.NUM) frames += 2;
            else frames += 1;
        }
        return frames;
    }

    public Either<String, Integer> storeInsn() {
        return Match(this).of(
                Case($(decimal()), Either.right(Opcodes.DSTORE)),
                Case($(integer()), Either.right(Opcodes.ISTORE)),
                Case($(bool()), Either.right(Opcodes.ISTORE)),
                Case($(empty()), Either.left("Cannot store empty expression")),
                Case($(), Either.right(Opcodes.ASTORE))
        );
    }


    public Either<String, Integer> loadInsn() {
        return Match(this).of(
                Case($(decimal()), Either.right(Opcodes.DLOAD)),
                Case($(integer()), Either.right(Opcodes.ILOAD)),
                Case($(bool()), Either.right(Opcodes.ILOAD)),
                Case($(empty()), Either.left("Cannot store empty expression")),
                Case($(), Either.right(Opcodes.ALOAD))
        );
    }


    public Either<String, Integer> retInsn() {
        return Match(this).of(
                Case($(decimal()), Either.right(DRETURN)),
                Case($(integer()), Either.right(IRETURN)),
                Case($(bool()), Either.right(IRETURN)),
                Case($(empty()), Either.right(RETURN)),
                Case($(), Either.right(ARETURN))
        );
    }

    public boolean isSimple() {
        return this.types.size() == 1;
    }

    public String classDescriptor() {
        StringBuilder sig = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            sig.append(types.get(i).descriptorChar());
            Tuple2<Signature, Signature> gen = generic.get(i);
            if (!gen._1.equals(empty())) {
                sig.append("_").append(gen._1.classDescriptor()).append("_");
            }
            if (!gen._2.equals(empty())) {
                sig.append("__").append(gen._2.classDescriptor()).append("__");
            }
        }
        return sig.toString();
    }

    public String internalDescriptor() {
        StringBuilder sig = new StringBuilder();

        for (int i = 0; i < types.size(); i++) {
            sig.append(types.get(i).descriptor());
        }
        return sig.toString();
    }

    public static Signature fromType(Type clazz) {
        if (clazz.equals(void.class)) {
            return Signature.empty();
        } else {
            return parseType(clazz);
        }
    }

    private static Signature parseType(Type clazz) {
        if (clazz.equals(String.class)) {
            return Signature.string();
        } else if (clazz.equals(int.class)) {
            return Signature.integer();
        } else if (clazz.equals(double.class)) {
            return Signature.decimal();
        } else if (clazz.equals(boolean.class)) {
            return Signature.bool();
        } else if (clazz.equals(com.dfsek.substrate.environment.IO.class)) {
            if (clazz instanceof ParameterizedType parameterizedType) {
                return Signature.io().applyGenericReturn(0, fromTypeGeneric(parameterizedType.getActualTypeArguments()[0]));
            }
            return Signature.io();
        } else {
            throw new IllegalArgumentException("Illegal class: " + clazz);
        }
    }

    public static Signature fromTypeGeneric(Type clazz) {
        if (clazz.equals(void.class) || clazz.equals(Void.class)) {
            return Signature.empty();
        } else return parseType(clazz);
    }

    public static Signature fromRecord(Class<? extends Record> record) {
        return Stream.ofAll(Arrays.stream(record.getRecordComponents())).foldLeft(Signature.empty(), (a, b) -> a.and(Signature.fromType(b.getType())));
    }
}
