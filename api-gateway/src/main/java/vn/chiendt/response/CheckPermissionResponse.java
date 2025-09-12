package vn.chiendt.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CheckPermissionResponse implements Serializable {
    private int status;
    private String path;
    private String message;
}
