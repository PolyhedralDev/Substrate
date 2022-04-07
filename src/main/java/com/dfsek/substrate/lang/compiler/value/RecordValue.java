package com.dfsek.substrate.lang.compiler.value;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lexer.read.Position;
import io.vavr.collection.List;
import io.vavr.control.Either;

/**
 * Value fetched from the record passed as input
 */
public class RecordValue implements Value {
    private final Signature ref;
    private final Class<? extends Record> inputClass;
    private final int index;

    public RecordValue(Signature ref, Class<? extends Record> inputClass, int index) {
        this.ref = ref;
        this.inputClass = inputClass;
        this.index = index;
    }

    @Override
    public Signature reference() {
        return ref;
    }

    @Override
    public List<Either<CompileError, Op>> load(BuildData data) {
        return List.of(Op.aLoad(1))
                .append(Op.checkCast(CompilerUtil.internalName(inputClass)))
                .append(Op.invokeVirtual(
                        CompilerUtil.internalName(inputClass),
                        inputClass.getRecordComponents()[index].getName(),
                        "()" + ref.internalDescriptor()));
    }
}