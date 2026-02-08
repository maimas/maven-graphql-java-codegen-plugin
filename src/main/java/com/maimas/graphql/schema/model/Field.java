package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
    private boolean deprecated;

    @JsonProperty("deprecationReason")
    private String deprecationReason;

    @JsonProperty("defaultValue")
    private String defaultValue;
}
