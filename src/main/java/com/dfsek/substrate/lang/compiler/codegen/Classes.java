package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.Environment;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;

import static com.dfsek.substrate.lang.compiler.util.CompilerUtil.internalName;

public class Classes {
    public static final String RECORD = internalName(Record.class);
    public static final String STRING = internalName(String.class);
    public static final String LAMBDA = internalName(Lambda.class);
    public static final String TUPLE = internalName(Tuple.class);
    public static final String SCRIPT = internalName(Script.class);
    public static final String ENVIRONMENT = CompilerUtil.internalName(Environment.class);
}
