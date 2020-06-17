package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Schema {

    @JsonProperty("queryType")
    private QueryType queryType;

    @JsonProperty("mutationType")
    private MutationType mutationType;

    @JsonProperty("subscriptionType")
    private SubscriptionType subscriptionType;

    @JsonProperty("types")
    List<Type> types = new ArrayList<>();

    @JsonProperty("directives")
    List<Directive> directives = new ArrayList<>();
}
