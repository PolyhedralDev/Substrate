package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.DataType;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

public class ListNode extends ExpressionNode {
    private final List<ExpressionNode> elements;
    private final Position position;

    public ListNode(List<ExpressionNode> elements, Position position) {
        this.elements = elements;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        Signature signature = elements.get(0).referenceType(data);
        System.out.println("SIG: " + signature);
        System.out.println(elements.get(0));
        elements.forEach(arg -> {
            if(!arg.referenceType(data).equals(signature)) {
                throw new ParseException("Array element mismatch. Expected " + signature + ", got " + arg.referenceType(data), position);
            }
        });

        CompilerUtil.pushInt(elements.size(), visitor);
        Signature params = elements.get(0).referenceType(data);
        params.getType(0).applyNewArray(visitor, params.getGenericReturn(0));
        for (int i = 0; i < elements.size(); i++) {
            visitor.visitInsn(DUP); // duplicate reference for all elements.
            CompilerUtil.pushInt(i, visitor); // push index
            elements.get(i).applyReferential(visitor, data); // apply value
            visitor.visitInsn(params.getType(0).arrayStoreInsn());
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature returnType(BuildData data) {
        return new Signature(DataType.LIST).applyGenericReturn(0, elements.get(0).returnType(data));
    }
}
