package com.dfsek.substrate.lang.compiler.codegen;

import com.dfsek.substrate.environment.Environment;
import com.dfsek.substrate.Script;
import com.dfsek.substrate.environment.io.IOFunctionInt2Obj;
import com.dfsek.substrate.environment.io.IOFunctionNum2Obj;
import com.dfsek.substrate.environment.io.IOFunctionObj2Obj;
import com.dfsek.substrate.environment.io.IOFunctionUnit2Obj;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import io.vavr.collection.List;

import static com.dfsek.substrate.lang.compiler.util.CompilerUtil.internalName;

public class Classes {
    public static final String RECORD = internalName(Record.class);
    public static final String STRING = internalName(String.class);
    public static final String LAMBDA = internalName(Lambda.class);
    public static final String TUPLE = internalName(Tuple.class);
    public static final String SCRIPT = internalName(Script.class);
    public static final String ENVIRONMENT = internalName(Environment.class);
    public static final String OBJECT = internalName(Object.class);

    public static final String LIST = internalName(List.class);
    public static final String INTEGER = internalName(Integer.class);
    public static final String DOUBLE = internalName(Double.class);
    public static final String IO = internalName(com.dfsek.substrate.environment.IO.class);
    public static final String IO_FUNCTION = internalName(IOFunctionObj2Obj.class);
    public static final String IO_FUNCTION_INT = internalName(IOFunctionInt2Obj.class);
    public static final String IO_FUNCTION_NUM = internalName(IOFunctionNum2Obj.class);
    public static final String IO_FUNCTION_UNIT = internalName(IOFunctionUnit2Obj.class);
}
