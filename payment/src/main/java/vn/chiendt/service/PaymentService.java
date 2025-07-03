package vn.chiendt.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.chiendt.dto.response.PaymentIntentResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j(topic = "PAYMENT-SERVICE")
public class PaymentService {

    @Autowired
    public PaymentService(@Value("${stripe.secret-key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    /**
     * PaymentIntent is a key concept used to manage the lifecycle of a payment. It represents an attempt to collect payment from a customer
     * and is designed to handle complex payment flows, including multi-step authentication (e.g., 3D Secure) and retries for failed payments.
     *
     * @param amount
     * @param currency
     * @return return the clientSecret to the frontend
     * @throws StripeException
     */
    public PaymentIntentResponse createPaymentIntent(Long amount, String currency) throws StripeException {
        log.info("createPaymentIntent");

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("payment_method_types", paymentMethodTypes);

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return PaymentIntentResponse.builder()
                .paymentId(paymentIntent.getId())
                .clientSecret(paymentIntent.getClientSecret()) // Return the clientSecret to the frontend
                .build();
    }

    /**
     * Refund money to customer by paymentIntentId
     *
     * @param paymentIntentId
     * @return return refund id
     * @throws StripeException
     */
    public String createRefund(String paymentIntentId) throws StripeException {
        log.info("Stripe service createRefund");

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .build();

        Refund refund = Refund.create(params);

        return refund.getId();
    }

    /**
     * Confirm Payment Intent
     *
     * @param paymentId
     * @return
     * @throws StripeException
     */
    public String confirmPaymentIntent(String paymentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        PaymentIntent confirmedIntent = paymentIntent.confirm();
        return confirmedIntent.getStatus();
    }

}
