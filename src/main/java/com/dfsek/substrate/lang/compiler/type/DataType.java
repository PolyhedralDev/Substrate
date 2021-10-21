package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.tokenizer.Token;
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
            return "L"+ReflectionUtil.internalName(Lambda.class)+";";
        }

        @Override
        public char descriptorChar() {
            return 'F';
        }
    },
    TUP {
        @Override
        public String descriptor() {
            return "L"+ReflectionUtil.internalName(Tuple.class)+";";
        }

        @Override
        public char descriptorChar() {
            return 'T';
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

    public static DataType fromToken(Token token) {
        if(token.getType() == Token.Type.BOOL_TYPE) {
            return BOOL;
        } else if(token.getType() == Token.Type.INT_TYPE) {
            return INT;
        } else if(token.getType() == Token.Type.STRING_TYPE) {
            return STR;
        } else if(token.getType() == Token.Type.NUM_TYPE) {
            return NUM;
        } else if(token.getType() == Token.Type.FUN_TYPE) {
            return FUN;
        }
        throw new IllegalArgumentException("Invalid token: " + token);
    }
}
