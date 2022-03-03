package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.util.Pair;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.Objects;

import static io.vavr.API.*;


public class Signature implements Opcodes {
    private static final Signature VOID = new Signature();
    private static final Signature BOOL = new Signature(DataType.BOOL);
    private static final Signature INT = new Signature(DataType.INT);
    private static final Signature DECIMAL = new Signature(DataType.NUM);
    private static final Signature STRING = new Signature(DataType.STR);

    private static final Signature FUN = new Signature(DataType.FUN);

    private static final Signature LIST = new Signature(DataType.LIST);
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
        if (!(obj instanceof Signature)) return false;
        Signature that = (Signature) obj;

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
            if (this.types.get(i) != that.types.get(i)) return false;
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

    public Either<String, Integer> arrayStoreInsn() {
        return Match(this).of(
                Case($(decimal()), Either.right(Opcodes.DASTORE)),
                Case($(integer()), Either.right(Opcodes.IASTORE)),
                Case($(bool()), Either.right(Opcodes.BASTORE)),
                Case($(empty()), Either.left("Cannot store empty expression")),
                Case($(), Either.right(Opcodes.AASTORE))
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

    public Either<String, Integer> arrayLoadInsn() {
        return Match(this).of(
                Case($(decimal()), Either.right(Opcodes.DALOAD)),
                Case($(integer()), Either.right(Opcodes.IALOAD)),
                Case($(bool()), Either.right(Opcodes.BALOAD)),
                Case($(empty()), Either.left("Cannot store empty expression")),
                Case($(), Either.right(Opcodes.AALOAD))
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

    public Either<CompileError, Op> newArrayInsn(Position position) {
        if (isSimple()) {
            return getType(0).applyNewArray(this);
        } else {
            return Op.aNewArray(CompilerUtil.internalName(Tuple.class));
        }
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
            if (types.get(i) == DataType.LIST) {
                sig.append(getGenericReturn(i).internalDescriptor());
            }
        }
        return sig.toString();
    }
}
