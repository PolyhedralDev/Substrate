package com.dfsek.substrate.lang.compiler.codegen.ops;

import com.dfsek.substrate.lang.compiler.type.Signature;

public final class LocalVariable {
    private final Signature type;
    private final int width;
    private final int offset;
    private final String name;

    public LocalVariable(Signature type, int width, int offset, String name) {
        this.type = type;
        this.width = width;
        this.offset = offset;
        this.name = name;
    }

    public Signature getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public String getName() {
        return name;
    }

    public int getOffset() {
        return offset;
    }
}
