package vn.chiendt.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransactionType {

    @JsonProperty("in")
    IN,
    @JsonProperty("out")
    OUT,
    @JsonProperty("initial_stock")
    INITIAL_STOCK
}
