package vn.chiendt.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import vn.chiendt.dto.request.ChargeRequest;
import vn.chiendt.dto.request.PaymentIntentRequest;
import vn.chiendt.dto.response.PaymentIntentResponse;

public interface PaymentService {
    PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException;

    String createRefund(String paymentIntentId) throws StripeException;

    String confirmPaymentIntent(String paymentId) throws StripeException;

    Charge charge(ChargeRequest request) throws StripeException;
}
