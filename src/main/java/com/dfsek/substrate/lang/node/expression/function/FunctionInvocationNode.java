package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.function.Function;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class FunctionInvocationNode extends ExpressionNode {
    private final Token id;
    private final List<ExpressionNode> arguments;

    public FunctionInvocationNode(Token id, List<ExpressionNode> arguments) {
        this.id = id;
        this.arguments = arguments;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such function: " + id.getContent(), id.getPosition());
        }
        Value value = data.getValue(id.getContent());
        if (!(value instanceof Function)) {
            throw new ParseException("Value \"" + id.getContent() + "\" is not a function.", id.getPosition());
        }

        Function function = (Function) value;

        Signature argSignature;
        if (arguments.isEmpty()) {
            argSignature = Signature.empty();
        } else if (arguments.size() == 1) {
            argSignature = arguments.get(0).reference(data).expandTuple();
        } else {
            argSignature = arguments.get(0).reference(data).expandTuple();
            for (int i = 1; i < arguments.size(); i++) {
                argSignature = argSignature.and(arguments.get(i).reference(data).expandTuple());
            }
        }

        function.preArgsPrep(visitor, data);

        arguments.forEach(arg -> arg.apply(visitor, data));


        if (!function.argsMatch(argSignature)) {
            throw new ParseException("Argument signature mismatch. \"" + id.getContent() + "\" Expects " + function.arguments() + ", got " + argSignature, id.getPosition());
        }


        function.invoke(visitor, data, argSignature, arguments);
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature reference(BuildData data) {
        return data.getValue(id.getContent()).reference().getGenericReturn(0);
    }
}
