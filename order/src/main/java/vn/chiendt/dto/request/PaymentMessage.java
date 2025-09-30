package vn.chiendt.dto.request;

import lombok.Setter;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;


@Setter
public class PaymentMessage {
    private String orderId;
    private Long customerId;
    private Long amount;
    private Currency currency;
    private PaymentMethod paymentMethod;
}
