package com.dfsek.substrate.lang;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lexer.read.Positioned;
import com.dfsek.substrate.parser.ParserScope;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;


public interface Node extends Opcodes, Positioned {
    static boolean disableOptimisation() {
        String p = System.getProperty("substrate.DisableOptimisation");
        return p != null && p.equals("true");
    }

    List<Either<CompileError, Op>> apply(BuildData data, ParserScope scope) throws ParseException;

    default Node simplify() {
        return this;
    }
}
