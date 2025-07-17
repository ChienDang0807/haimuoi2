package vn.chiendt.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Platform {
    @JsonProperty("web")
    WEB,
    @JsonProperty("ios")
    IOS,
    @JsonProperty("android")
    ANDROID,
    @JsonProperty("mini_app")
    MINI_APP
}
