package com.dfsek.substrate.util.pair;

import java.util.Objects;


public class Pair<L, R> {
    private L left;
    private R right;
    
    private Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L1, R1> Pair<L1, R1> of(L1 left, R1 right) {
        return new Pair<>(left, right);
    }
    
    public ImmutablePair<L, R> immutable() {
        return ImmutablePair.of(left, right);
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
        if(!(obj instanceof Pair)) return false;
        
        Pair<?, ?> that = (Pair<?, ?>) obj;
        return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
    }
}
