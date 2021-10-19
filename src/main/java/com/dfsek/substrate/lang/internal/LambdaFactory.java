package com.dfsek.substrate.lang.internal;

public class LambdaFactory {
    public static final class Signature {
        private final int args;
        private final int returnSignature;

        public Signature(int args, int returnSignature) {
            this.args = args;
            this.returnSignature = returnSignature;
        }
    }
}
