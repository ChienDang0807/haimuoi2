package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;

import java.io.Serializable;
import java.util.List;

@Getter
@ToString
public class PlaceOrderRequest implements Serializable {

    @NotNull(message = "customerId must be not null")
    public Long customerId;

    @NotNull(message = "amount must be not null")
    private Long amount;

    @NotNull(message = "currency must be not null")
    private Currency currency;

    @NotEmpty(message = "orderItems must be not empty")
    private List<OrderItemRequest> orderItems;

}
