package vn.chiendt.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vn.chiendt.dto.request.PaymentIntentRequest;

import java.util.Map;

@FeignClient(name = "payment-service", url = "${api.internal.paymentUrl}")
public interface PaymentServiceClient {

    @PostMapping("/create-payment-intent")
    ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentIntentRequest request);

    @PostMapping("/refund/{paymentId}")
    ResponseEntity<Map<String, String>> createRefund(@PathVariable String paymentId);
}
