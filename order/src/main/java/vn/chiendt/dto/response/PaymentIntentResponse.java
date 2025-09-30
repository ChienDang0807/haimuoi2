package vn.chiendt.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class PaymentIntentResponse implements Serializable {

    private String paymentId;
    private String clientSecret;
}
