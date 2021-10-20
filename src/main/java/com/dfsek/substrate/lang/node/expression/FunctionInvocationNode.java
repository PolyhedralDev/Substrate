package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.BuildData;
import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.Function;
import com.dfsek.substrate.lang.compiler.Signature;
import com.dfsek.substrate.lang.compiler.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class FunctionInvocationNode implements Node {
    private final Token id;
    private final List<ExpressionNode> arguments;

    public FunctionInvocationNode(Token id, List<ExpressionNode> arguments) {
        this.id = id;
        this.arguments = arguments;
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

        Function function = (Function) value;

        Signature argSignature;
        if(arguments.isEmpty()) {
            argSignature = Signature.empty();
        } else if(arguments.size() == 1) {
            argSignature = arguments.get(0).returnType(data);
        } else {
            argSignature = arguments.get(0).returnType(data);
            for (int i = 1; i < arguments.size(); i++) {
                argSignature = argSignature.and(arguments.get(i).returnType(data));
            }
        }

        if(!argSignature.equals(function.arguments())) {
            throw new ParseException("Argument signature mismatch. Expected " + function.arguments() + ", got " + argSignature, id.getPosition());
        }

        function.preArgsPrep(visitor, data);

        arguments.forEach(arg -> arg.apply(visitor, data));

        function.invoke(visitor, data);
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }
}
