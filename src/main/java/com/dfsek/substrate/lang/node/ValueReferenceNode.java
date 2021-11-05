package com.dfsek.substrate.lang.node;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.lambda.LocalLambdaReferenceFunction;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.compiler.value.Function;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.FunctionInvocationNode;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ValueReferenceNode extends ExpressionNode {
    private final Token id;

    public ValueReferenceNode(Token id) {
        this.id = id;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }

        Value value = data.getValue(id.getContent());
        if (value.reference().getGenericReturn(0).size() <= 1) {
            load(visitor, data);
        } else {
            for (int i = 0; i < value.reference().getGenericReturn(0).size(); i++) {
                load(visitor, data);

                visitor.visitMethodInsn(INVOKEVIRTUAL,
                        CompilerUtil.internalName(Tuple.class) + "IMPL_" + value.reference().getGenericReturn(0).classDescriptor(),
                        "param" + i,
                        "()" + value.reference().getGenericReturn(0).getType(i).descriptor(),
                        false);
            }
        }
    }

    @Override
    public void applyReferential(MethodVisitor visitor, BuildData data) {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        load(visitor, data);
    }

    private void load(MethodVisitor visitor, BuildData data) {
        if (data.isShadowed(id.getContent())) {
            Value value = data.getShadowValue(id.getContent());
            visitor.visitVarInsn(ALOAD, 0);
            visitor.visitFieldInsn(GETFIELD,
                    data.getClassName(),
                    "scope" + data.getShadowField(id.getContent()),
                    value.reference().internalDescriptor());
        } else {
            Value value = data.getValue(id.getContent());
            if (value instanceof Function && !(value instanceof LocalLambdaReferenceFunction)) {
                Function function = (Function) value;

                List<ExpressionNode> internalArgs = new ArrayList<>();

                AtomicInteger offset = new AtomicInteger(1);

                for (int i = 0; i < function.arguments().size(); i++) {
                    int finalI = i;
                    internalArgs.add(new ExpressionNode() {
                        @Override
                        public Signature referenceType(BuildData data) {
                            return new Signature(function.arguments().getType(finalI))
                                    .applyGenericReturn(0, function.arguments().getGenericReturn(finalI))
                                    .applyGenericArgument(0, function.arguments().getGenericArguments(finalI));
                        }

                        @Override
                        public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
                            DataType type = function.arguments().getType(finalI);
                            visitor.visitVarInsn(type.loadInsn(), offset.getAndAdd(new Signature(type).frames()));
                        }

                        @Override
                        public Position getPosition() {
                            return ValueReferenceNode.this.getPosition();
                        }
                    });
                }

                Class<?> delegate = data.lambdaFactory().implement(function.arguments(),
                        function.reference().getSimpleReturn(),
                        Signature.empty(),
                        (method, clazz) -> {
                            new FunctionInvocationNode(this.id, internalArgs).apply(method, data);
                            method.visitInsn(RETURN);
                        });

                visitor.visitTypeInsn(NEW, CompilerUtil.internalName(delegate));
                visitor.visitInsn(DUP);
                visitor.visitMethodInsn(INVOKESPECIAL,
                        CompilerUtil.internalName(delegate),
                        "<init>",
                        "()V",
                        false);

            } else {
                int offset = data.offset(id.getContent());
                visitor.visitVarInsn(value.reference().getType(0).loadInsn(), offset);
            }
        }
    }

    @Override
    public Position getPosition() {
        return id.getPosition();
    }

    @Override
    public Signature referenceType(BuildData data) {
        if (!data.valueExists(id.getContent())) {
            throw new ParseException("No such value: " + id.getContent(), id.getPosition());
        }
        return data.getValue(id.getContent()).reference();
    }
}
