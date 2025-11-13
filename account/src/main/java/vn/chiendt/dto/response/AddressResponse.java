package vn.chiendt.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class AddressResponse {
    private Long id;
    private String addressType;
    private Long userId;
    private String apartmentNumber;
    private String floor;
    private String building;
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    private Date createdAt;
    private Date updatedAt;
}
