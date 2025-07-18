package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
public class PlaceOrderRequest implements Serializable {

    @NotNull(message = "customerId must be not null")
    public Long customerId;

    @NotNull(message = "amount must be not null")
    private BigDecimal amount;

    @NotNull(message = "currency must be not null")
    private String currency;

    @NotNull(message = "paymentMethod must be not null")
    private String paymentMethod;

    @NotEmpty(message = "orderItems must be not empty")
    private List<OrderItemRequest> orderItems;

}
