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
    private final List<ExpressionNode> args;
    private final Position position;

    public ListNode(List<ExpressionNode> args, Position position) {
        this.args = args;
        this.position = position;
    }

    @Override
    public void apply(MethodVisitor visitor, BuildData data) throws ParseException {
        if(args.stream().map(node -> node.returnType(data)).distinct().count() > 1) {
            throw new ParseException("", position);
        }
        CompilerUtil.pushInt(args.size(), visitor);
        Signature params = args.get(0).returnType(data);
        if(params.isSimple()) {
            params.getType(0).applyNewArray(visitor, params.getGenericReturn(0));
            for (int i = 0; i < args.size(); i++) {
                visitor.visitInsn(DUP); // duplicate reference for all elements.
                CompilerUtil.pushInt(i, visitor); // push index
                args.get(i).apply(visitor, data); // apply value
                visitor.visitInsn(params.getType(0).arrayStoreInsn());
            }
        }
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Signature returnType(BuildData data) {
        return new Signature(DataType.LIST).applyGenericReturn(0, args.get(0).returnType(data));
    }
}
