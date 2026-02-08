package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MutationType {

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
