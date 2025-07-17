package vn.chiendt.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import vn.chiendt.common.Platform;
import vn.chiendt.exception.ValidationException;

import java.io.Serializable;

@Getter
@Setter
public class SignInRequest implements Serializable {
    @NotBlank(message = "user must be not blank")
    private String username;

    @NotBlank(message = "user must be not blank")
    private String password;

    private Platform platform; // web, ios, android, mini_app

    private String deviceToken; // for push notify

    private String versionApp;

    public void validate() {
        if (platform != null || platform != Platform.WEB) {
            if (!StringUtils.hasLength(deviceToken)) {
                throw new ValidationException("deviceToken must not be blank");
            }
            if (!StringUtils.hasLength(versionApp)) {
                throw new ValidationException("versionApp must not be blank");
            }
        }
    }
}
