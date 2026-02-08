package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

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

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public MutationType getMutationType() {
        return mutationType;
    }

    public void setMutationType(MutationType mutationType) {
        this.mutationType = mutationType;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }
}
