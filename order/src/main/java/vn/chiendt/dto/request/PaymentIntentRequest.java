package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;

import java.io.Serializable;

@Getter
@Setter
public class PaymentIntentRequest implements Serializable {

    @NotNull(message = "customerId must be not null")
    private Long customerId;

    @NotNull(message = "orderId must be not null")
    private String orderId;

    @NotNull(message = "paymentMethod must be not blank")
    private PaymentMethod paymentMethod;

    @NotNull(message = "amount must be not null")
    private Long amount;

    @NotNull(message = "currency must be not null")
    private Currency currency;

    @NotBlank(message = "description must be not blank")
    private String description;
}
