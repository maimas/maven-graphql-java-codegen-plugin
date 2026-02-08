package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Directive {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("locations")
    private List<Object> locations;

    @JsonProperty("args")
    private List<Arg> args;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Object> getLocations() {
        return locations;
    }

    public void setLocations(List<Object> locations) {
        this.locations = locations;
    }

    public List<Arg> getArgs() {
        return args;
    }

    public void setArgs(List<Arg> args) {
        this.args = args;
    }
}
