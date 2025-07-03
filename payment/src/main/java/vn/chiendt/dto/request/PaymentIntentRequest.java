package vn.chiendt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PaymentIntentRequest implements Serializable {
    @NotNull(message = "amount must be not null")
    private Long amount;

    @NotBlank(message = "currency must be not blank")
    private String currency;

    @NotBlank(message = "paymentMethodId must be not blank")
    private String paymentMethodId;
}
