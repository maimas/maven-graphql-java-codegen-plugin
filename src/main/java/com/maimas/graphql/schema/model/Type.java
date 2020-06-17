package com.maimas.graphql.schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maimas.graphql.schema.model.enums.KindType;
import lombok.Data;

import java.util.List;

@Data
public class Type {
    @JsonProperty("kind")
    private KindType kind;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("fields")
    private List<Field> fields;

    @JsonProperty("inputFields")
    private List<Field> inputFields;

    @JsonProperty("interfaces")
    private List<Object> interfaces;

    @JsonProperty("enumValues")
    private List<EnumValue> enumValues;

    @JsonProperty("possibleTypes")
    private List<Object> possibleTypes;

}
