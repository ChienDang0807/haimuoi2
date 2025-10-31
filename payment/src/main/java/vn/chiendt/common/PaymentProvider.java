package vn.chiendt.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PaymentProvider {
    @JsonProperty("stripe")
    STRIPE,
    @JsonProperty("in_house")
    IN_HOUSE,
    @JsonProperty("unknown")
    UNKNOWN
}


