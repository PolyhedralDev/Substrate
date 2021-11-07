package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.value.function.Function;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class FunctionInvocationNode extends ExpressionNode {
    private final List<ExpressionNode> arguments;
    private final ExpressionNode function;

    public FunctionInvocationNode(ExpressionNode function, List<ExpressionNode> arguments) {
        this.arguments = arguments;
        this.function = function;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        ParserUtil.checkWeakReferenceType(function, data, Signature.fun());

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

        function.apply(visitor, data);
    }

    @Override
    public Position getPosition() {
        return function.getPosition();
    }

    @Override
    public Signature reference(BuildData data) {
        return function.reference(data).getSimpleReturn();
    }
}
