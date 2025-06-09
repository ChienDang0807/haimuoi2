package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.chiendt.common.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class UpdateOrderRequest {
    private String id;

    @NotNull(message = "amount must be not null")
    private BigDecimal amount;

    @NotNull(message = "currency must be not null")
    private String currency;

    @NotNull(message = "paymentMethod must be not null")
    private String paymentMethod;

    private OrderStatus status;

    @NotEmpty(message = "orderItems must be not empty")
    private List<OrderItemRequest> orderItems;
}
