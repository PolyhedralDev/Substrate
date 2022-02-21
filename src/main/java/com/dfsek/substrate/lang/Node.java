package com.dfsek.substrate.lang;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Positioned;
import org.objectweb.asm.Opcodes;


public interface Node extends Opcodes, Positioned {
    void apply(MethodBuilder builder, BuildData data) throws ParseException;

    default Node simplify() {
        return this;
    }

    static boolean disableOptimisation() {
        String p = System.getProperty("substrate.DisableOptimisation");
        return p != null && p.equals("true");
    }
}
