package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.compiler.codegen.ops.MethodBuilder;
import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.lexer.token.Token;
import com.dfsek.substrate.lexer.token.TokenType;
import org.objectweb.asm.Opcodes;

public enum DataType implements Opcodes {
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
            return ILOAD;
        }

        @Override
        public int storeInsn() {
            return ISTORE;
        }

        public int returnInsn() {
            return IRETURN;
        }

        @Override
        public int arrayStoreInsn() {
            return IASTORE;
        }

        @Override
        public int arrayLoadInsn() {
            return IALOAD;
        }

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            visitor.newArray(T_INT);
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
            return DLOAD;
        }

        @Override
        public int storeInsn() {
            return DSTORE;
        }

        public int returnInsn() {
            return DRETURN;
        }

        @Override
        public int arrayStoreInsn() {
            return DASTORE;
        }

        @Override
        public int arrayLoadInsn() {
            return DALOAD;
        }

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            visitor.newArray(T_DOUBLE);
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

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            visitor.aNewArray("java/lang/String");
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
            return ILOAD;
        }

        @Override
        public int storeInsn() {
            return ISTORE;
        }

        public int returnInsn() {
            return IRETURN;
        }

        @Override
        public int arrayStoreInsn() {
            return BASTORE;
        }

        @Override
        public int arrayLoadInsn() {
            return BALOAD;
        }

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            visitor.newArray(T_BOOLEAN);
        }
    },
    FUN {
        @Override
        public String descriptor() {
            return "L" + CompilerUtil.internalName(Lambda.class) + ";";
        }

        @Override
        public char descriptorChar() {
            return 'F';
        }

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            visitor.aNewArray(CompilerUtil.internalName(Lambda.class));
        }
    },
    LIST {
        @Override
        public String descriptor() {
            return "[";
        }

        @Override
        public char descriptorChar() {
            return 'A';
        }

        @Override
        public void applyNewArray(MethodBuilder visitor, Signature generic) {
            StringBuilder arr = new StringBuilder("[");
            nestArray(arr, generic);
            visitor.aNewArray(arr.toString());
        }

        private void nestArray(StringBuilder arr, Signature generic) {
            if (generic.isSimple()) {
                if (generic.getType(0).equals(INT)) {
                    arr.append('I');
                } else if (generic.getType(0).equals(NUM)) {
                    arr.append('D');
                } else if (generic.getType(0).equals(BOOL)) {
                    arr.append('Z');
                } else if (generic.getType(0).equals(STR)) {
                    arr.append("Ljava/lang/String;");
                } else if (generic.getType(0).equals(FUN)) {
                    arr.append('L').append(CompilerUtil.internalName(Lambda.class)).append(';');
                } else if (generic.getType(0).equals(LIST)) {
                    Signature nested = generic.getGenericReturn(0);
                    nestArray(arr, nested);
                }
            } else {
                if (generic.equals(Signature.empty())) {
                    throw new IllegalArgumentException("Cannot construct array of VOID");
                }
                arr.append("L").append(CompilerUtil.internalName(Tuple.class)).append(";"); // It's a tuple.
            }
        }
    };

    public static DataType fromToken(Token token) {
        if (token.getType() == TokenType.BOOL_TYPE) {
            return BOOL;
        } else if (token.getType() == TokenType.INT_TYPE) {
            return INT;
        } else if (token.getType() == TokenType.STRING_TYPE) {
            return STR;
        } else if (token.getType() == TokenType.NUM_TYPE) {
            return NUM;
        } else if (token.getType() == TokenType.FUN_TYPE) {
            return FUN;
        } else if (token.getType() == TokenType.LIST_TYPE) {
            return LIST;
        }
        throw new IllegalArgumentException("Invalid token: " + token);
    }

    public abstract String descriptor();

    public abstract char descriptorChar();

    public int loadInsn() {
        return ALOAD;
    }

    public int storeInsn() {
        return ASTORE;
    }

    public int returnInsn() {
        return ARETURN;
    }

    public int arrayStoreInsn() {
        return AASTORE;
    }

    public int arrayLoadInsn() {
        return AALOAD;
    }

    public abstract void applyNewArray(MethodBuilder visitor, Signature generic);
}
