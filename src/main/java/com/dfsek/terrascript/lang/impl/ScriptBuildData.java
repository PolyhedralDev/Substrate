package com.dfsek.terrascript.lang.impl;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.lang.internal.Operation;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

public class ScriptBuildData implements BuildData {
    private final String generatedName;
    private final Map<String, VariableMeta> variableTypeMap = new HashMap<>();
    private int index = 0;

    public ScriptBuildData(String generatedName) {
        this.generatedName = generatedName;
    }

    @Override
    public String generatedClassName() {
        return generatedName;
    }

    public void register(Token id, Operation.ReturnType type) throws ParseException {
        if(variableTypeMap.containsKey(id.getContent())) throw new ParseException("Duplicate variable ID: " + id, id.getPosition());
        System.out.println(id.getContent());
        variableTypeMap.put(id.getContent(), new VariableMeta(type, index++));
    }

    public int getVariableIndex(String id) {
        System.out.println(id);
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

        public VariableMeta(Operation.ReturnType type, int index) {
            this.type = type;
            this.index = index;
        }
    }
}
