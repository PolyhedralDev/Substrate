package com.dfsek.substrate.lang.node.expression.list;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.parser.ParserUtil;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListNode extends ExpressionNode {
    private final List<ExpressionNode> elements;
    private final Position position;

    public ListNode(List<ExpressionNode> elements, Position position) {
        this.elements = elements;
        this.position = position;
    }

    @Override
    public void apply(MethodBuilder builder, BuildData data) throws ParseException {
        Signature signature = elements.get(0).reference(data);
        elements.forEach(element -> ParserUtil.checkReferenceType(element, data, signature));

        builder.pushInt(elements.size());
        Signature elementSignature = elements.get(0).reference(data);
        if(elementSignature.isSimple()) {
            elementSignature.getType(0).applyNewArray(builder, elementSignature);
        } else {
            builder.aNewArray(CompilerUtil.internalName(Tuple.class));
        }

        for (int i = 0; i < elements.size(); i++) {
            builder.dup(); // duplicate reference for all elements.
            builder.pushInt(i); // push index
            elements.get(i).applyReferential(builder, data); // apply value
            if(elementSignature.isSimple()) {
                builder.insn(elementSignature.getType(0).arrayStoreInsn());
            } else {
                builder.aastore();
            }
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

    @Override
    public List<ExpressionNode> contents() {
        return elements;
    }
}
