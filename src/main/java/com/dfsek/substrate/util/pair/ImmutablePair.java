package com.dfsek.substrate.util.pair;

import java.util.Objects;


public final class ImmutablePair<L, R> {
    private static final ImmutablePair<?, ?> NULL = new ImmutablePair<>(null, null);
    private final L left;
    private final R right;

    private ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L1, R1> ImmutablePair<L1, R1> of(L1 left, R1 right) {
        return new ImmutablePair<>(left, right);
    }

    @SuppressWarnings("unchecked")
    public static <L1, R1> ImmutablePair<L1, R1> ofNull() {
        return (ImmutablePair<L1, R1>) NULL;
    }

    public Pair<L, R> mutable() {
        return Pair.of(left, right);
    }

    public R getRight() {
        return right;
    }

    public L getLeft() {
        return left;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ImmutablePair)) return false;

        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) obj;
        return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
    }
}
