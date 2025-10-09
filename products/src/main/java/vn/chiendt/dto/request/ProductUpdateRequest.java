package vn.chiendt.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import lombok.ToString;
import vn.chiendt.common.ProductStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@ToString
public class ProductUpdateRequest implements Serializable {
    @NotNull(message = "Product ID is required for update.")
    private Long id;

    @NotBlank(message = "Product name cannot be blank.")
    @Size(max = 255, message = "Product name must not exceed 255 characters.")
    private String name;

    @Size(max = 255, message = "Slug must not exceed 255 characters.")
    private String slug;

    @Size(max = 10000, message = "Description is too long.")
    private String description;

    @NotNull(message = "Price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero.")
    @Digits(integer = 13, fraction = 2, message = "Price must have up to 13 digits and 2 decimals.")
    private BigDecimal price;

    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    // Optionally allow updating attributes (JSON)
    private Map<String, String> attributes;

    // Optionally allow updating status
    private ProductStatus status;
}
