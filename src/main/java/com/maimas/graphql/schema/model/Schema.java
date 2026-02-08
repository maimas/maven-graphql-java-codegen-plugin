package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Schema {

    @JsonProperty("queryType")
    private QueryType queryType;

    @JsonProperty("mutationType")
    private MutationType mutationType;

    @JsonProperty("subscriptionType")
    private SubscriptionType subscriptionType;

    @JsonProperty("types")
    private List<Type> types = new ArrayList<>();

    @JsonProperty("directives")
    private List<Directive> directives = new ArrayList<>();
}
