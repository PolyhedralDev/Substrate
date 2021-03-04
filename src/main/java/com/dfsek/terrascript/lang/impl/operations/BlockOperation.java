package com.dfsek.terrascript.lang.impl.operations;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class BlockOperation implements Operation {
    private final List<Operation> ops;
    private final Position position;

    public BlockOperation(List<Operation> ops, Position position) {
        this.ops = ops;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        for (Operation op : ops) {
            op.apply(visitor, data);
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
