package com.dfsek.substrate.lang;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lexer.read.Positioned;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;


public interface Node extends Opcodes, Positioned {
    static boolean disableOptimisation() {
        String p = System.getProperty("substrate.DisableOptimisation");
        return p != null && p.equals("true");
    }

    List<Either<CompileError, Op>> apply(BuildData data) throws ParseException;

    default Node simplify() {
        return this;
    }
}
