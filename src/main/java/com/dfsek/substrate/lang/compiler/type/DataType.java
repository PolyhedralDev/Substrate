package com.dfsek.substrate.lang.compiler.type;

import com.dfsek.substrate.lang.compiler.codegen.Classes;
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
            return ILOAD;
        }

        @Override
        public int storeInsn() {
            return ISTORE;
        }

        public int returnInsn() {
            return IRETURN;
        }

    },
    FUN {
        @Override
        public String descriptor() {
            return "L" + Classes.LAMBDA + ";";
        }

        @Override
        public char descriptorChar() {
            return 'F';
        }

    },
    LIST {
        @Override
        public String descriptor() {
            return "L" + Classes.LIST + ";";
        }

        @Override
        public char descriptorChar() {
            return 'L';
        }

    },

    IO {
        @Override
        public String descriptor() {
            return "L" + Classes.IO + ";";
        }

        @Override
        public char descriptorChar() {
            return 'E';
        }
    },

    ANY {
        @Override
        public String descriptor() {
            throw new IllegalStateException("Cannot use ANY type in class.");
        }

        @Override
        public char descriptorChar() {
            return '*';
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
        } else if (token.getType() == TokenType.IO) {
            return IO;
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

}
