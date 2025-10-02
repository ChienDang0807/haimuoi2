package vn.chiendt.cartservice.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CartState {
    ACTIVE("active"),
    COMPLETED("completed"),
    FAILED("failed"),
    PENDING("pending")
    ;

    private final String value;

    CartState(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue(){
        return value;
    }

    @JsonCreator // Jackson: khi đọc từ JSON/Mongo sang Enum
    public static CartState fromValue(String value) {
        for (CartState cs : values()) {
            if (cs.value.equalsIgnoreCase(value)) {
                return cs;
            }
        }
        throw new IllegalArgumentException("Invalid CartState: " + value);
    }
}
