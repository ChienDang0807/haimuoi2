package vn.chiendt.dto.request;

import lombok.Setter;

import java.math.BigDecimal;

@Setter
public class PaymentMessage {
    private String orderId;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
}
