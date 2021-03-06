package com.dfsek.substrate.lang.internal;

import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public interface Operation extends Opcodes {
    default void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }

    default ReturnType getType() {
        return ReturnType.VOID;
    }

    enum ReturnType {
        VOID, STR, BOOL, NUM
    }

    Position getPosition();
}
