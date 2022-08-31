package com.dfsek.substrate.lang.node.expression.binary.bool;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.compiler.value.Value;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.constant.BooleanNode;
import com.dfsek.substrate.lang.node.expression.error.ErrorNode;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.exception.ParseException;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Label;

public class BooleanAndNode extends BooleanOperationNode {
    private BooleanAndNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<BooleanAndNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new BooleanAndNode(left, right, op));
    }
    @Override
    public boolean apply(boolean left, boolean right) {
        return left && right;
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation() || left instanceof ErrorNode || right instanceof ErrorNode) return this;
        if (left instanceof BooleanNode) {
            if (((BooleanNode) left).getValue()) {
                return right; // left does not matter.
            } else {
                return left; // short-circuit
            }
        }
        if (right instanceof BooleanNode) {
            if (((BooleanNode) right).getValue()) {
                return left; // right does not matter.
            } else {
                return right; // short-circuit
            }
        }
        return super.simplify();
    }

    @Override
    public List<Either<CompileError, Op>> apply(BuildData data, LinkedHashMap<String, Value> values) throws ParseException {
        Label shortFalse = new Label();
        Label end = new Label();
        return left.apply(data, values)
                .append(Op.ifEQ(shortFalse))
                .appendAll(right.apply(data, values))
                .append(Op.ifEQ(shortFalse))
                .append(Op.pushTrue())
                .append(Op.goTo(end))
                .append(Op.label(shortFalse))
                .append(Op.pushFalse())
                .append(Op.label(end));
    }
}
