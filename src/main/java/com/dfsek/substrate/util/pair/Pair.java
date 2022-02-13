package com.dfsek.substrate.util.pair;

import java.util.Objects;


public final class Pair<L, R> {
    private static final Pair<?, ?> NULL = new Pair<>(null, null);
    private final L left;
    private final R right;

    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L1, R1> Pair<L1, R1> of(L1 left, R1 right) {
        return new Pair<>(left, right);
    }

    @SuppressWarnings("unchecked")
    public static <L1, R1> Pair<L1, R1> ofNull() {
        return (Pair<L1, R1>) NULL;
    }

    public Mutable<L, R> mutable() {
        return Mutable.of(left, right);
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
        if (!(obj instanceof Pair)) return false;

        Pair<?, ?> that = (Pair<?, ?>) obj;
        return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }

    public static class Mutable<L, R> {
        private L left;
        private R right;

        private Mutable(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L1, R1> Mutable<L1, R1> of(L1 left, R1 right) {
            return new Mutable<>(left, right);
        }

        public Pair<L, R> immutable() {
            return Pair.of(left, right);
        }

        public L getLeft() {
            return left;
        }

        public void setLeft(L left) {
            this.left = left;
        }

        public R getRight() {
            return right;
        }

        public void setRight(R right) {
            this.right = right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Pair.Mutable)) return false;

            Mutable<?, ?> that = (Mutable<?, ?>) obj;
            return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
        }
    }
}
