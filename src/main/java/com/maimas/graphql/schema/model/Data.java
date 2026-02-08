package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Data {

    @JsonProperty("__schema")
    private Schema schema;
}
