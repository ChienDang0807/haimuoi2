package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SaleOrderItemCreationRequest implements Serializable {
    @NotNull(message = "salesId must be not null")
    private String salesId;

    @NotNull(message = "productId must be not null")
    private Long productId;

    @NotNull(message = "quantity must be not null")
    private Integer quantity;

    @NotNull(message = "price must be not null")
    private Long price;
}
