package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EnumValue {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isDeprecated")
    private boolean isDeprecated;

    @JsonProperty("deprecationReason")
    private String deprecationReason;
}
