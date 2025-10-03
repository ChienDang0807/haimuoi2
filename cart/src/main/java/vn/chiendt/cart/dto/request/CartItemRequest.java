package vn.chiendt.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequest {

    @NotBlank(message = "ProductId must not be blank")
    private Long productId;

    @NotBlank(message = "Product name must not be blank")
    private String productName;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be >= 0")
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotBlank(message = "ShopId must not be blank")
    private String shopId;

    private Double discount;

}
