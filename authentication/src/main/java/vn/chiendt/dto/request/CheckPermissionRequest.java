package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CheckPermissionRequest implements Serializable {
    @NotNull(message = "username must be not null")
    private String username;
    @NotNull(message = "path must be not null")
    private String path;
}
