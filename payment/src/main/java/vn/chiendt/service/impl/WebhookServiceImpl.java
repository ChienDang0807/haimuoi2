package vn.chiendt.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.chiendt.common.TransactionStatus;
import vn.chiendt.service.TransactionService;
import vn.chiendt.service.WebhookService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "WEBHOOK-SERVICE")
public class WebhookServiceImpl implements WebhookService {

    private final TransactionService transactionService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String updateOrderStatus;


    @Override
    public void handlePaymentIntentCreated(Event event) {
        String paymentId = getPaymentId(event);
        log.info("Payment created: {}", paymentId);
    }

    @Override
    public void handlePaymentIntentCanceled(Event event) {
        log.info("Payment canceled");
        String paymentId = getPaymentId(event);
        transactionService.updateTransactionStatus(paymentId, TransactionStatus.CANCELED);
    }

    @Override
    public void handlePaymentIntentSucceeded(Event event) {
        log.info("Payment succeeded");
        String paymentId = getPaymentId(event);

        // update status of transaction
        transactionService.updateTransactionStatus(paymentId, TransactionStatus.SUCCEEDED);

        // Synchronize with order-service
        String orderId = transactionService.getOrderId(paymentId);
        Map<String, String> massage = new HashMap<>();
        massage.put("orderId", orderId);
        massage.put("status", "PAID");

        kafkaTemplate.send(updateOrderStatus, new Gson().toJson(massage));
    }

    @Override
    public void handlePaymentIntentFailed(Event event) {
        log.info("Payment failed");
        String paymentId = getPaymentId(event);
        transactionService.updateTransactionStatus(paymentId, TransactionStatus.FAILED);
    }

    /**
     * Get payment by event
     *
     * @param event
     * @return
     */
    private String getPaymentId(Event event) {
        log.info("getPaymentId by event: {}", event.getId());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        String rawJson = dataObjectDeserializer.getRawJson();
        JsonObject jsonObject = new Gson().fromJson(rawJson, JsonObject.class);
        String paymentId = jsonObject.get("id").getAsString();

        log.info("Payment id: {}", paymentId);

        return paymentId;
    }
}