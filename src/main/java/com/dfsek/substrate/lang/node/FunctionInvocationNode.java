package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.Function;
import com.dfsek.substrate.lang.compiler.Value;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

public class FunctionInvocationNode implements Node {
    private final Token id;

    public FunctionInvocationNode(Token id) {
        this.id = id;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(!data.valueExists(id.getContent())) {
            throw new ParseException("No such function: " + id.getContent(), id.getPosition());
        }
        Value value = data.getValue(id.getContent());
        if(!(value instanceof Function)) {
            throw new ParseException("Value \"" + id.getContent() + "\" is not a function.", id.getPosition());
        }
        ((Function) value).invoke(visitor, data);
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }
}
