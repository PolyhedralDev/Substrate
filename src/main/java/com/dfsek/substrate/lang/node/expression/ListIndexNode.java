package com.dfsek.substrate.lang.node.expression;

import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Position;
import org.objectweb.asm.MethodVisitor;

public class ListIndexNode extends ExpressionNode {
    private final ExpressionNode listReference;
    private final ExpressionNode index;

    public ListIndexNode(ExpressionNode listReference, ExpressionNode index) {
        this.listReference = listReference;
        this.index = index;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        listReference.apply(visitor, data);
        index.apply(visitor, data);

        if(!index.referenceType(data).equals(Signature.integer())) {
            throw new ParseException("Expected INT, got " + index.referenceType(data), index.getPosition());
        }

        Signature ref = referenceType(data);

        if(!listReference.referenceType(data).weakEquals(Signature.list())) {
            throw new ParseException("Expected LIST<?>, got " + listReference.referenceType(data), listReference.getPosition());
        }

        if(ref.isSimple()) {
            visitor.visitInsn(ref.getType(0).arrayLoadInsn());
        } else {
            visitor.visitInsn(AALOAD);
        }
    }

    @Override
    public Position getPosition() {
        return index.getPosition();
    }

    @Override
    public Signature referenceType(BuildData data) {
        return listReference.referenceType(data).getSimpleReturn();
    }
}
