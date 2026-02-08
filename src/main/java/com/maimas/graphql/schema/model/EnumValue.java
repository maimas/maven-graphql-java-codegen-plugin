package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnumValue {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isDeprecated")
    private boolean deprecated;

    @JsonProperty("deprecationReason")
    private String deprecationReason;
}
