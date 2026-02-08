package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldType {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ofType")
    private FieldType ofType;
}
