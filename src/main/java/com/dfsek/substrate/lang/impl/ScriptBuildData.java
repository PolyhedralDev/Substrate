package com.dfsek.substrate.lang.impl;

import com.dfsek.substrate.lang.internal.BuildData;
import com.dfsek.substrate.lang.internal.Operation;
import com.dfsek.substrate.parser.exception.ParseException;
import com.dfsek.substrate.tokenizer.Token;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import java.util.Map;

public class ScriptBuildData implements BuildData {
    private final String generatedName;
    private final Map<String, VariableMeta> variableTypeMap = new HashMap<>();
    private int index = 2;
    private final Label start = new Label();
    private final Label end = new Label();

    public ScriptBuildData(String generatedName) {
        this.generatedName = generatedName;
    }

    @Override
    public String generatedClassName() {
        return generatedName;
    }

    public void register(Token id, Operation.ReturnType type) throws ParseException {
        if(variableTypeMap.containsKey(id.getContent())) throw new ParseException("Duplicate variable ID: " + id, id.getPosition());
        variableTypeMap.put(id.getContent(), new VariableMeta(type, index, id.getContent()));
        switch(type) {
            case BOOL:
            case STR:
                index++;
                break;
            case NUM:
                index+=2;
                break;
        }
    }

    @Override
    public void initialize(MethodVisitor visitor) {
        visitor.visitLabel(start);
    }

    @Override
    public void finalize(MethodVisitor visitor) {
        visitor.visitLabel(end);

        variableTypeMap.forEach(((s, variableMeta) -> {
            String descriptor;

            switch(variableMeta.type) {
                case NUM: {
                    descriptor = "D";
                    break;
                }
                case STR: {
                    descriptor = "Ljava/lang/String;";
                    break;
                }
                case BOOL: {
                    descriptor = "Z";
                    break;
                }

                default:throw new IllegalArgumentException("Illegal type: " + variableMeta.type);
            }

            visitor.visitLocalVariable(variableMeta.id, descriptor, null, start, end, variableMeta.index);
        }));
    }

    public int getVariableIndex(String id) {
        return variableTypeMap.get(id).index;
    }

    public Operation.ReturnType getVariableType(String id) {
        return variableTypeMap.get(id).type;
    }

    public int getVarSize() {
        return variableTypeMap.size();
    }

    public static class VariableMeta {
        private final Operation.ReturnType type;
        private final int index;
        private final String id;

        public VariableMeta(Operation.ReturnType type, int index, String id) {
            this.type = type;
            this.index = index;
            this.id = id;
        }
    }
}
