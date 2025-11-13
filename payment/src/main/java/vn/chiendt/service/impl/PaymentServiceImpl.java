package vn.chiendt.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.chiendt.common.Currency;
import vn.chiendt.common.PaymentMethod;
import vn.chiendt.common.PaymentProvider;
import vn.chiendt.common.TransactionStatus;
import vn.chiendt.dto.request.ChargeRequest;
import vn.chiendt.dto.request.PaymentIntentRequest;
import vn.chiendt.dto.response.PaymentIntentResponse;
import vn.chiendt.model.Transaction;
import vn.chiendt.service.PaymentService;
import vn.chiendt.service.TransactionService;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j(topic = "PAYMENT-SERVICE")
public class PaymentServiceImpl implements PaymentService {

    private final TransactionService transactionService;

    @Autowired
    public PaymentServiceImpl(@Value("${stripe.secret-key}") String secretKey, TransactionService transactionService) {
        Stripe.apiKey = secretKey;
        this.transactionService = transactionService;
    }


    @Override
    public PaymentIntentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException {
        log.info("createPaymentIntent");

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add(request.getPaymentMethod().getValue());

        Map<String, Object> params = new HashMap<>();
        params.put("amount", request.getAmount());
        params.put("currency", request.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Save transaction
        saveTransaction(request.getCustomerId(), paymentIntent.getId(), request.getPaymentMethod(), request.getAmount(), request.getCurrency(), request.getDescription());


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
    @Override
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
    @Override
    public String confirmPaymentIntent(String paymentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        PaymentIntent confirmedIntent = paymentIntent.confirm();
        return confirmedIntent.getStatus();
    }

    @Override
    public Charge charge(ChargeRequest request) throws StripeException {
        // Create a charge request
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(String.valueOf(request.getCurrency()).toLowerCase())
                .setDescription(request.getDescription())
                .setSource(request.getToken()) // Token from the frontend
                .build();

        Charge charge = Charge.create(params);

        // Save transaction
        saveTransaction(request.getCustomerId(), charge.getId(), request.getPaymentMethod(), request.getAmount(), request.getCurrency(), request.getDescription());

        return charge;
    }

    /**
     * Save transaction
     *
     * @param customerId
     * @param paymentId
     * @param paymentMethod
     * @param amount
     * @param currency
     * @param description
     */
    private void saveTransaction(Long customerId, String paymentId, PaymentMethod paymentMethod, Long amount, Currency currency, String description) {
        log.info("saveTransaction for paymentId: {}", paymentId);

        Transaction transaction = new Transaction();
        if (customerId != null) {
            transaction.setCustomerId(customerId);
        }
        transaction.setPaymentId(paymentId);
        transaction.setPaymentProvider(PaymentProvider.STRIPE);
        transaction.setProviderPaymentId(paymentId);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setDescription(description);
        transaction.setStatus(TransactionStatus.CREATED);

        transactionService.createTransaction(transaction);
    }
}
