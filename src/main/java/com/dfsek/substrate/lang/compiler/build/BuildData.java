package com.dfsek.substrate.lang.compiler.build;

import com.dfsek.substrate.lang.compiler.lambda.LambdaFactory;
import com.dfsek.substrate.lang.compiler.tuple.TupleFactory;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.parser.DynamicClassLoader;
import com.dfsek.substrate.util.Lazy;
import com.dfsek.substrate.util.pair.Pair;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BuildData {
    private final TupleFactory tupleFactory;
    private final LambdaFactory lambdaFactory;

    private final Map<String, Value> values;
    private final Map<String, Pair<Integer, Value>> shadowFields;
    private final Map<Pair<BuildData, String>, Integer> valueOffsets;

    private final BuildData parent;
    private final DynamicClassLoader classLoader;
    private final ClassWriter classWriter;
    private final ValueInterceptor interceptor;

    private final Lazy<String> name;
    private int offset;

    private int shadowField = 0;

    private final int implArgsOffset;

    public BuildData(DynamicClassLoader classLoader, ClassWriter classWriter, String name) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        tupleFactory = new TupleFactory(classLoader);
        lambdaFactory = new LambdaFactory(classLoader, tupleFactory);
        this.name = Lazy.of(() -> name);
        values = new HashMap<>();
        valueOffsets = new HashMap<>();
        shadowFields = new HashMap<>();
        parent = null;
        interceptor = (a, b) -> {
        };
        this.offset = 2;
        this.implArgsOffset = 1;
    }

    private BuildData(DynamicClassLoader classLoader,
                      ClassWriter classWriter,
                      TupleFactory tupleFactory,
                      LambdaFactory lambdaFactory,
                      Map<String, Value> values,
                      Map<String, Pair<Integer, Value>> shadowFields, Map<Pair<BuildData, String>, Integer> valueOffsets,
                      BuildData parent,
                      ValueInterceptor interceptor,
                      Function<BuildData, String> name, int offset, int implArgsOffset) {
        this.classLoader = classLoader;
        this.classWriter = classWriter;
        this.tupleFactory = tupleFactory;
        this.lambdaFactory = lambdaFactory;
        this.values = values;
        this.shadowFields = shadowFields;
        this.valueOffsets = valueOffsets;
        this.parent = parent;
        this.interceptor = interceptor;
        this.name = Lazy.of(() -> name.apply(this));
        this.offset = offset;
        this.implArgsOffset = implArgsOffset;
    }

    public LambdaFactory lambdaFactory() {
        return lambdaFactory;
    }

    public TupleFactory tupleFactory() {
        return tupleFactory;
    }

    public void registerValue(String id, Value value) {
        if (values.containsKey(id))
            throw new IllegalArgumentException("Value with identifier \"" + id + "\" already registered.");
        values.put(id, value);
        valueOffsets.put(Pair.of(this, id), offset);
    }

    public void shadowValue(String id, Value value) {
        if (!values.containsKey(id))
            throw new IllegalArgumentException("Value with identifier \"" + id + "\" not registered.");
        shadowFields.put(id, Pair.of(shadowField++, value));
    }

    public boolean isShadowed(String id) {
        return shadowFields.containsKey(id);
    }

    public int getShadowField(String id) {
        return shadowFields.get(id).getLeft();
    }

    public Value getShadowValue(String id) {
        return shadowFields.get(id).getRight();
    }

    public String getClassName() {
        return name.get();
    }

    protected Map<String, Value> getValues() {
        return values;
    }

    public void registerValue(String id, Value value, int frames) {
        registerValue(id, value);
        offset += frames;
    }

    public void offsetInc(int offset) {
        this.offset += offset;
    }

    public int getOffset() {
        return offset;
    }

    public boolean hasOffset(String id) {
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(Pair.of(test, id))) {
            if (test == null) return false;
            test = test.parent;
        }

        return true;
    }

    public Value getValue(String id) {
        interceptor.fetch(id, this);
        if (!values.containsKey(id)) {
            throw new IllegalArgumentException("No such value \"" + id + "\": " + values);
        }
        return values.get(id);
    }

    public int offset(String id) {
        interceptor.fetch(id, this);
        if (!values.containsKey(id)) throw new IllegalArgumentException("No such value \"" + id + "\"");

        BuildData test = this;
        while (!valueOffsets.containsKey(Pair.of(test, id))) test = test.parent;

        return valueOffsets.get(Pair.of(test, id));
    }

    public boolean valueExists(String id) {
        interceptor.fetch(id, this);
        return values.containsKey(id) || shadowFields.containsKey(id);
    }

    public BuildData sub() {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                shadowFields, valueOffsets, // but same JVM scope
                this,
                interceptor,
                ignore -> name.get(), offset, implArgsOffset);
    }

    public void loadImplementationArguments(MethodVisitor visitor) {
        visitor.visitVarInsn(Opcodes.ALOAD, implArgsOffset);
    }

    public BuildData detach(ValueInterceptor interceptor, Function<BuildData, String> name, int args) {
        return new BuildData(classLoader,
                classWriter,
                tupleFactory,
                lambdaFactory,
                new HashMap<>(values), // new scope
                new HashMap<>(), new HashMap<>(), // *and* different JVM scope
                this,
                interceptor,
                name,
                1, args + 1);
    }
}
