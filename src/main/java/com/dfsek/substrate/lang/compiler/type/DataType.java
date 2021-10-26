package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.compiler.util.CompilerUtil;
import com.dfsek.substrate.lang.internal.Lambda;
import com.dfsek.substrate.lang.internal.Tuple;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public enum DataType implements Opcodes{
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
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitIntInsn(NEWARRAY, T_INT);
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
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitIntInsn(NEWARRAY, T_DOUBLE);
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
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitTypeInsn(ANEWARRAY, "java/lang/String");
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
            return IASTORE;
        }

        @Override
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitIntInsn(NEWARRAY, T_BOOLEAN);
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
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitTypeInsn(ANEWARRAY, CompilerUtil.internalName(Lambda.class));
        }
    },
    TUP {
        @Override
        public String descriptor() {
            return "L" + CompilerUtil.internalName(Tuple.class) + ";";
        }

        @Override
        public char descriptorChar() {
            return 'T';
        }
        @Override
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            visitor.visitTypeInsn(ANEWARRAY, CompilerUtil.internalName(Tuple.class));
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
        public void applyNewArray(MethodVisitor visitor, Signature generic) {
            StringBuilder arr = new StringBuilder("[[");
            int dims = nestArray(arr, generic, 2);
            visitor.visitMultiANewArrayInsn(arr.toString(), dims);
        }

        private int nestArray(StringBuilder arr, Signature generic, int dimensions) {
            if(generic.isSimple()) {
                if(generic.getType(0).equals(INT)) {
                    arr.append('I');
                } else if(generic.getType(0).equals(NUM)) {
                    arr.append('D');
                } else if(generic.getType(0).equals(BOOL)) {
                    arr.append('Z');
                } else if(generic.getType(0).equals(STR)) {
                    arr.append("Ljava/lang/String;");
                } else if(generic.getType(0).equals(FUN)) {
                    arr.append(CompilerUtil.internalName(Lambda.class));
                } else if(generic.getType(0).equals(TUP)) {
                    arr.append(CompilerUtil.internalName(Tuple.class));
                } else if(generic.getType(0).equals(LIST)) {
                    Signature nested = generic.getGenericReturn(0);
                    return nestArray(arr, nested, dimensions+1);
                }
            } else {
                if(generic.equals(Signature.empty())) {
                    throw new IllegalArgumentException("Cannot construct array of VOID");
                }
                arr.append(CompilerUtil.internalName(Tuple.class)); // It's a tuple.
            }
            return dimensions;
        }
    };

    public static DataType fromToken(Token token) {
        if (token.getType() == Token.Type.BOOL_TYPE) {
            return BOOL;
        } else if (token.getType() == Token.Type.INT_TYPE) {
            return INT;
        } else if (token.getType() == Token.Type.STRING_TYPE) {
            return STR;
        } else if (token.getType() == Token.Type.NUM_TYPE) {
            return NUM;
        } else if (token.getType() == Token.Type.FUN_TYPE) {
            return FUN;
        } else if (token.getType() == Token.Type.LIST_TYPE) {
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

    public abstract void applyNewArray(MethodVisitor visitor, Signature generic);
}
