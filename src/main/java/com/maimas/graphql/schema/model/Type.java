package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maimas.graphql.schema.model.enums.KindType;

import java.util.List;

public class Type {
    @JsonProperty("kind")
    private KindType kind;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("fields")
    private List<Field> fields;

    @JsonProperty("inputFields")
    private List<Field> inputFields;

    @JsonProperty("interfaces")
    private List<Object> interfaces;

    @JsonProperty("enumValues")
    private List<EnumValue> enumValues;

    @JsonProperty("possibleTypes")
    private List<Object> possibleTypes;

    public KindType getKind() {
        return kind;
    }

    public void setKind(KindType kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getInputFields() {
        return inputFields;
    }

    public void setInputFields(List<Field> inputFields) {
        this.inputFields = inputFields;
    }

    public List<Object> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Object> interfaces) {
        this.interfaces = interfaces;
    }

    public List<EnumValue> getEnumValues() {
        return enumValues;
    }

    public void setEnumValues(List<EnumValue> enumValues) {
        this.enumValues = enumValues;
    }

    public List<Object> getPossibleTypes() {
        return possibleTypes;
    }

    public void setPossibleTypes(List<Object> possibleTypes) {
        this.possibleTypes = possibleTypes;
    }
}
