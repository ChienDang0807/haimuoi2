package vn.chiendt.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import vn.chiendt.common.TransactionStatus;
import vn.chiendt.service.TransactionService;
import vn.chiendt.service.WebhookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
@Slf4j(topic = "WEBHOOK-CONTROLLER")
@Tag(name = "Webhook Controller")
public class WebhookController {

    private final TransactionService transactionService;
    private final WebhookService webhookService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Operation(summary = "Webhook", description = "API for handle Stripe events")
    @PostMapping
    public String handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        //log.info("Received Webhook Payload: {}", payload);

        Event event = null;

        try {
            // Verify the webhook signature
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            return "Invalid signature";
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.created":
                webhookService.handlePaymentIntentCreated(event);
                break;
            case "payment_intent.canceled":
                webhookService.handlePaymentIntentCanceled(event);
                break;
            case "payment_intent.succeeded":
                webhookService.handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                webhookService.handlePaymentIntentFailed(event);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return "Success";
    }


}