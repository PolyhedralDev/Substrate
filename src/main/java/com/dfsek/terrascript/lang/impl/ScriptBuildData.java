package com.dfsek.terrascript.lang.impl;

import com.dfsek.terrascript.lang.internal.BuildData;
import com.dfsek.terrascript.parser.exception.ParseException;
import com.dfsek.terrascript.tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

public class ScriptBuildData implements BuildData {
    private final String generatedName;
    private final Map<String, VariableType> variableTypeMap = new HashMap<>();

    public ScriptBuildData(String generatedName) {
        this.generatedName = generatedName;
    }

    @Override
    public String generatedClassName() {
        return generatedName;
    }

    public void register(Token id, VariableType type) throws ParseException {
        if(variableTypeMap.containsKey(id.getContent())) throw new ParseException("Duplicate variable ID: " + id, id.getPosition());
        variableTypeMap.put(id.getContent(), type);
    }

    public enum VariableType {
        STRING, BOOL, NUM
    }

    public int getVarSize() {
        return variableTypeMap.size();
    }
}
