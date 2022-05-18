package com.dfsek.substrate.lang.node.expression.binary.arithmetic;

import com.dfsek.substrate.lang.Node;
import com.dfsek.substrate.lang.compiler.build.BuildData;
import com.dfsek.substrate.lang.compiler.codegen.CompileError;
import com.dfsek.substrate.lang.compiler.codegen.bytes.Op;
import com.dfsek.substrate.lang.compiler.type.Signature;
import com.dfsek.substrate.lang.compiler.type.Unchecked;
import com.dfsek.substrate.lang.node.expression.ExpressionNode;
import com.dfsek.substrate.lang.node.expression.binary.NumericBinaryNode;
import com.dfsek.substrate.lang.node.expression.constant.DecimalNode;
import com.dfsek.substrate.lang.node.expression.constant.IntegerNode;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.parser.ParserUtil;
import io.vavr.collection.List;
import io.vavr.control.Either;
import org.objectweb.asm.Opcodes;

public class AdditionNode extends NumericBinaryNode {
    private AdditionNode(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        super(left, right, op);
    }

    public static Unchecked<AdditionNode> of(Unchecked<? extends ExpressionNode> left, Unchecked<? extends ExpressionNode> right, Token op) {
        return Unchecked.of(new AdditionNode(left, right, op));
    }

    @Override
    public double apply(double left, double right) {
        return left + right;
    }

    @Override
    public int apply(int left, int right) {
        return left + right;
    }

    @Override
    protected ExpressionNode check(Unchecked<? extends ExpressionNode> unchecked) {
        return unchecked.get(Signature.decimal(), Signature.integer(), Signature.string());
    }

    @Override
    public List<Either<CompileError, Op>> applyOp(BuildData data) {
        Signature leftType = left.reference();

        if (leftType.equals(Signature.integer())) {
            return List.of(Op.iAdd());
        } else if (leftType.equals(Signature.decimal())) {
            return List.of(Op.dAdd());
        } else if (leftType.equals(Signature.string())) {
            return List.of(Op.invokeVirtual("java/lang/String",
                    "concat",
                    "(Ljava/lang/String;)Ljava/lang/String;"));
        }
        return List.of(Op.error("Unsupported type for addition operation: " + leftType, left.getPosition()));
    }

    @Override
    protected int intOp() {
        return Opcodes.IADD;
    }

    @Override
    protected int doubleOp() {
        return Opcodes.DADD;
    }

    public Signature reference() {
        Signature ref = left.reference();
        if (ref.weakEquals(Signature.fun())) {
            return ref.getGenericReturn(0);
        }
        return ref;
    }

    @Override
    public ExpressionNode simplify() {
        if (Node.disableOptimisation()) return this;
        if (left instanceof IntegerNode && ((IntegerNode) left).getValue() == 0) {
            return right; // 0 + a == a
        }
        if (left instanceof DecimalNode && ((DecimalNode) left).getValue() == 0) {
            return right; // 0 + a == a
        }

        if (right instanceof IntegerNode && ((IntegerNode) right).getValue() == 0) {
            return left; // a + 0 == a
        }
        if (right instanceof DecimalNode && ((DecimalNode) right).getValue() == 0) {
            return left; // a + 0 == a
        }
        return super.simplify();
    }
}
