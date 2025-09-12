package vn.chiendt.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class VerifyTokenResponse implements Serializable {
    private int status;
    private boolean isValid;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
}
