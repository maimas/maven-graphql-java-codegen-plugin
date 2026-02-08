package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldType {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ofType")
    private FieldType ofType;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getOfType() {
        return ofType;
    }

    public void setOfType(FieldType ofType) {
        this.ofType = ofType;
    }
}
