package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.util.ReflectionUtil;
import org.objectweb.asm.Opcodes;

public enum DataType {
    INT {
        @Override
        public String descriptor() {
            return "I";
        }

        @Override
        public char descriptorChar() {
            return 'I';
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
        public char descriptorChar() {
            return 'N';
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

        @Override
        public char descriptorChar() {
            return 'S';
        }
    },
    BOOL {
        @Override
        public String descriptor() {
            return "Z";
        }

        @Override
        public char descriptorChar() {
            return 'Z';
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
            return ReflectionUtil.internalName(Lambda.class);
        }

        @Override
        public char descriptorChar() {
            return 'F';
        }
    };

    public abstract String descriptor();

    public abstract char descriptorChar();

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
