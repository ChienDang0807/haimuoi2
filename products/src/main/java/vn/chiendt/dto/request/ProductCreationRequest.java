package vn.chiendt.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ProductCreationRequest implements Serializable {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String name;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Price must be a valid number with up to 2 decimal places")
    private BigDecimal price;

    private Long userId;

    @Size(max = 255, message = "Slug cannot exceed 255 characters")
    private String slug;

    // Optional JSON attributes
    private Map<String, String> attributes = new HashMap<>();
}
