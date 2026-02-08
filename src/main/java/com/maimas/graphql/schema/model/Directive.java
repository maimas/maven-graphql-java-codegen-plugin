package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Directive {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("locations")
    private List<Object> locations;

    @JsonProperty("args")
    private List<Arg> args;
}
