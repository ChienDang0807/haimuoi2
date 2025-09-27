package vn.chiendt.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Currency {
    @JsonProperty("usd")
    USD,
    @JsonProperty("eur")
    EUR,
    @JsonProperty("jpy")
    JPY,
    @JsonProperty("gbp")
    GBP,
    @JsonProperty("chf")
    CHF,
    @JsonProperty("vnd")
    VND;
}
