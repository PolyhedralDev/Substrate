package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
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
        Signature signature = elements.get(0).reference(data);
        elements.forEach(element -> {
            System.out.println(element.reference(data));
            //ParserUtil.checkReferenceType(element, data, signature);
        });

        CompilerUtil.pushInt(elements.size(), visitor);
        Signature elementSignature = elements.get(0).reference(data);
        elementSignature.getType(0).applyNewArray(visitor, elementSignature);
        for (int i = 0; i < elements.size(); i++) {
            visitor.visitInsn(DUP); // duplicate reference for all elements.
            CompilerUtil.pushInt(i, visitor); // push index
            elements.get(i).applyReferential(visitor, data); // apply value
            visitor.visitInsn(elementSignature.getType(0).arrayStoreInsn());
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature reference(BuildData data) {
        return Signature.list().applyGenericReturn(0, elements.get(0).reference(data));
    }
}
