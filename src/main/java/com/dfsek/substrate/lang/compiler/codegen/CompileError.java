package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.lexer.read.Position;
import com.dfsek.substrate.lexer.read.Positioned;

public interface CompileError extends Positioned {
    String message();
}
