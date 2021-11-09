package com.dfsek.substrate.lang.node.expression.function;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
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
        System.out.println("fun: " + function.reference(data));
        ParserUtil.checkWeakReferenceType(function, data, Signature.fun());

        Signature argSignature = CompilerUtil.expandArguments(data, arguments);

        if(!function.reference(data).getGenericArguments(0).equals(argSignature)) {
            throw new ParseException("Function argument mismatch, expected " + function.reference(data).getGenericArguments(0) + ", got " + argSignature, function.getPosition());
        }

        function.apply(visitor, data);

        arguments.forEach(arg -> arg.apply(visitor, data));


        Signature ref = reference(data);
        System.out.println("reference: " + ref);
        data.lambdaFactory().invoke(argSignature, ref.expandTuple(), data, visitor);

        if(ref.weakEquals(Signature.tup())) {
            data.offsetInc(1);
            int offset = data.getOffset();
            visitor.visitVarInsn(ASTORE, offset);

            Signature tup = ref.expandTuple();

            for (int i = 0; i < tup.size(); i++) {
                visitor.visitVarInsn(ALOAD, offset);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        CompilerUtil.internalName(data.tupleFactory().generate(tup)),
                        "param" + i,
                        "()" + tup.getType(i).descriptor(),
                        false);
            }
        }
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
