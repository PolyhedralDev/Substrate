package com.dfsek.substrate.lang;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public interface Node extends Opcodes {
    default void apply(MethodVisitor visitor, BuildData data) throws ParseException {

    }

    Position getPosition();
}
