package com.dfsek.terrascript.lang.internal;

import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public interface Operation extends Opcodes {
    default void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }
}
