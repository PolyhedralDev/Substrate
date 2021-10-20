package com.dfsek.substrate.lang.compiler;

import org.objectweb.asm.Opcodes;

public enum DataType {
    INT {
        @Override
        public String descriptor() {
            return "I";
        }

        @Override
        public int loadInsn() {
            return Opcodes.ILOAD;
        }

        @Override
        public int storeInsn() {
            return Opcodes.ISTORE;
        }

        public int returnInsn() {
            return Opcodes.IRETURN;
        }
    },
    NUM {
        @Override
        public String descriptor() {
            return "D";
        }

        @Override
        public int loadInsn() {
            return Opcodes.DLOAD;
        }

        @Override
        public int storeInsn() {
            return Opcodes.DSTORE;
        }

        public int returnInsn() {
            return Opcodes.DRETURN;
        }
    },
    STR {
        @Override
        public String descriptor() {
            return "Ljava/lang/String;";
        }
    },
    BOOL {
        @Override
        public String descriptor() {
            return "Z";
        }

        @Override
        public int loadInsn() {
            return Opcodes.ILOAD;
        }

        @Override
        public int storeInsn() {
            return Opcodes.ISTORE;
        }

        public int returnInsn() {
            return Opcodes.IRETURN;
        }
    },
    FUN {
        @Override
        public String descriptor() {
            return "Lcom/dfsek/substrate/lang/internal/Lambda;";
        }
    };

    public abstract String descriptor();

    public int loadInsn() {
        return Opcodes.ALOAD;
    }

    public int storeInsn() {
        return Opcodes.ASTORE;
    }

    public int returnInsn() {
        return Opcodes.ARETURN;
    }
}
