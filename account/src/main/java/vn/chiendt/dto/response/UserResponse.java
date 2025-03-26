package vn.chiendt.dto.response;


import lombok.*;
import vn.chiendt.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dob;
    private String username;
    private String email;
    private String phone;
}
