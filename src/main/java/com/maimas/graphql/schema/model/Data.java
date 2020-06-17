package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Data {

    @JsonProperty("__schema")
    private Schema schema;
}
