package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Field {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("args")
    private List<Arg> args;

    @JsonProperty("type")
    private FieldType type;

    @JsonProperty("isDeprecated")
    private boolean isDeprecated;

    @JsonProperty("deprecationReason")
    private String deprecationReason;

    @JsonProperty("defaultValue")
    private String defaultValue;

}
