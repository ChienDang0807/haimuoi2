package vn.chiendt.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.chiendt.common.Gender;
import vn.chiendt.common.UserType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    String firstName;
    @NotBlank(message = "lastName must be not blank")
    String lastName;

    Gender gender;

    Date dob;

    String phone;

    String email;

    String username;

    String password;

    UserType type;

    private List<AddressRequest> addresses; // home,office
}
