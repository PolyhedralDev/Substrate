package com.dfsek.terrascript.lang.internal;

import com.dfsek.terrascript.parser.exception.ParseException;
import org.objectweb.asm.MethodVisitor;


public interface Operation {
    default void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }
}
