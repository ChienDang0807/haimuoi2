package vn.chiendt.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentIntentResponse {
    private String paymentId;
    private String clientSecret;
}
