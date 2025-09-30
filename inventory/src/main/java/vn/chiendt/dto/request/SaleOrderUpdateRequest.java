package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.common.OrderStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class SaleOrderUpdateRequest implements Serializable {
    @NotNull(message = "productId must be not null")
    private String id;
    private Long customerId;
    private OrderStatus status;
    private Long totalAmount;
    private List<SaleOrderItemCreationRequest> items;
}
