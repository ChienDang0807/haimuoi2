package vn.chiendt.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Operation {
    @JsonProperty("equality")
    EQUALITY,
    @JsonProperty("negation")
    NEGATION,
    @JsonProperty("greater_than")
    GREATER_THAN,
    @JsonProperty("less_than")
    LESS_THAN,
    @JsonProperty("like")
    LIKE,
    @JsonProperty("starts_with")
    STARTS_WITH,
    @JsonProperty("ends_with")
    ENDS_WITH,
    @JsonProperty("contains")
    CONTAINS;
}
