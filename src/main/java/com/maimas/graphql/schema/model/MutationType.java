package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutationType {

    @JsonProperty("name")
    private String name;
}
