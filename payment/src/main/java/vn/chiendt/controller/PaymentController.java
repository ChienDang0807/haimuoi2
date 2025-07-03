package vn.chiendt.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.chiendt.dto.request.PaymentIntentRequest;
import vn.chiendt.dto.response.PaymentIntentResponse;
import vn.chiendt.service.PaymentService;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j(topic = "PAYMENT-CONTROLLER")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create payment intent", description = "API Pay for order")
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentIntentRequest request) throws StripeException {
        log.info("Create payment intent");

        PaymentIntentResponse response = paymentService.createPaymentIntent(request.getAmount(), request.getCurrency());

        return ResponseEntity.ok(Map.of("clientSecret", response.getClientSecret()));
    }

    @Operation(summary = "Confirm payment", description = "API check payment status")
    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, String>> confirmPaymentIntent(@RequestParam String paymentId) throws StripeException {
        log.info("Confirm payment intent");

        String status = paymentService.confirmPaymentIntent(paymentId);

        return ResponseEntity.ok(Map.of("status", status));
    }

    @Operation(summary = "Refund", description = "API refund money for customer")
    @PostMapping("/refund")
    public ResponseEntity<Map<String, String>> createRefund(@RequestParam String paymentId) throws StripeException {
        log.info("Refund payment intent");

        String refundId = paymentService.createRefund(paymentId);

        return ResponseEntity.ok(Map.of("refundId", refundId));
    }

}
